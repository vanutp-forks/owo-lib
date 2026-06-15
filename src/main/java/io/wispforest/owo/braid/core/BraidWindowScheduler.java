package io.wispforest.owo.braid.core;

import io.wispforest.owo.ui.event.ClientRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class BraidWindowScheduler {

    private static final List<App> APPS = new ArrayList<>();

    public static void add(BraidWindow window, AppState app) {
        APPS.add(new App(window, app));
    }

    private static void frame() {
        for (var app : new ArrayList<>(APPS)) {
            if (!app.state().running()) {
                app.state().dispose();

                APPS.remove(app);
                continue;
            }

            app.state().processEvents(
                MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration()
            );

            var ctx = new DrawContext(
                MinecraftClient.getInstance(),
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
            );
            ctx.translate(0, 0, -11000);

            app.state().draw(ctx);
        }
    }

    static {
        ClientRenderCallback.BEFORE_SWAP.register(client -> frame());
        // NeoForge has no equivalent to Fabric's ClientLifecycleEvents.CLIENT_STOPPING;
        // running apps are disposed individually once they stop (see frame()).
    }
}

record App(BraidWindow surface, AppState state) {}
