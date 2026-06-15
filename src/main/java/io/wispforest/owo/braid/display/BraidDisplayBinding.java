package io.wispforest.owo.braid.display;

import io.wispforest.owo.braid.core.events.MouseMoveEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.util.math.Vec3d;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.List;

public class BraidDisplayBinding {

    private static final List<BraidDisplay> ACTIVE_DISPLAYS = new ArrayList<>();

    // ---

    public static void activate(BraidDisplay display) {
        ACTIVE_DISPLAYS.add(display);
    }

    public static void deactivate(BraidDisplay display) {
        ACTIVE_DISPLAYS.remove(display);
    }

    // ---

    public static @Nullable DisplayHitResult targetDisplay;

    @ApiStatus.Internal
    public static @Nullable DisplayHitResult queryTargetDisplay(Vec3d rayOrigin, Vec3d rayDirection) {
        DisplayHitResult closestResult = null;
        double closestRayOffset = Double.POSITIVE_INFINITY;

        for (var display : ACTIVE_DISPLAYS) {
            var result = display.quad.hitTest(rayOrigin, rayDirection);
            if (result == null || result.t() >= closestRayOffset) continue;

            closestResult = new DisplayHitResult(display, result.point());
            closestRayOffset = result.t();
        }

        return closestResult;
    }

    @ApiStatus.Internal
    public static void onDisplayHit(DisplayHitResult targetDisplay) {
        var app = targetDisplay.display.app;

        var cursorX = targetDisplay.point.x() * app.surface.width();
        var cursorY = targetDisplay.point.y() * app.surface.height();

        app.eventBinding.add(new MouseMoveEvent(cursorX, cursorY));
    }

    @ApiStatus.Internal
    public static void updateAndDrawDisplays() {
        for (var display : ACTIVE_DISPLAYS) {
            display.updateAndDrawApp();
        }
    }

    // ---

    static {
        NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

            var consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            for (var display : ACTIVE_DISPLAYS) {
                if (!display.renderAutomatically) continue;

                var matrices = event.getPoseStack();
                var offset = display.quad.pos.subtract(event.getCamera().getPos());

                //noinspection DataFlowIssue
                matrices.push();
                matrices.translate(offset.x, offset.y, offset.z);

                //noinspection DataFlowIssue
                display.render(matrices, consumers, LightmapTextureManager.MAX_LIGHT_COORDINATE);

                matrices.pop();
            }
        });
    }

    // ---

    public record DisplayHitResult(BraidDisplay display, Vector2dc point) {}
}
