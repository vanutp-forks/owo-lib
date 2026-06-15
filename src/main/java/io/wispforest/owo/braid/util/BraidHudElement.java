package io.wispforest.owo.braid.util;

import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.framework.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.Nullable;

public class BraidHudElement {

    public final Widget widget;
    private AppState app;

    public BraidHudElement(Widget widget) {
        this.widget = widget;

        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
            this.setupAppState();
        });

        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
            this.resetAppState();
        });
    }

    public @Nullable AppState app() {
        return this.app;
    }

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (this.app == null) {
            if (!Owo.DEBUG) {
                return;
            }

            throw new IllegalStateException("tried to render a BraidHudElement before it was initialized");
        }

        this.app.processEvents(tickCounter.getLastFrameDuration());
        this.app.draw(context);
    }

    protected void setupAppState() {
        this.app = new AppState(
            null,
            AppState.formatName("BraidHudElement", widget),
            MinecraftClient.getInstance(),
            new Surface.Default(),
            new EventBinding.Headless(),
            widget
        );
    }

    protected void resetAppState() {
        this.app.dispose();
        this.app = null;
    }
}
