package io.wispforest.owo.braid.util.layers;

import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.braid.core.events.*;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.eventstream.BraidEventStream;
import io.wispforest.owo.braid.widgets.overlay.Overlay;
import io.wispforest.owo.braid.widgets.stack.Stack;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BraidLayersBinding {

    public static void add(Predicate<Screen> screenPredicate, Widget widget) {
        LAYERS.add(new Layer(screenPredicate, widget));
    }

    // ---

    @ApiStatus.Internal
    public static boolean tryHandleEvent(Screen screen, UserEvent event) {
        var app = ((OwoScreenExtension) screen).owo$getBraidLayersApp();
        if (app == null) {
            return false;
        }

        var slot = app.eventBinding.add(event);
        app.processEvents(0);

        return slot.handled();
    }

    @ApiStatus.Internal
    public static void renderLayers(Screen screen, DrawContext context, double mouseX, double mouseY) {
        var state = ((OwoScreenExtension) screen).owo$getBraidLayersState();
        if (state == null) {
            return;
        }

        state.refreshEvents.sink().onEvent(Unit.INSTANCE);
        state.app.eventBinding.add(new MouseMoveEvent(mouseX, mouseY));

        state.app.processEvents(MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration());
        state.app.draw(context);
    }

    private static void setupLayers(Screen screen) {
        var widgets = LAYERS.stream().filter(layer -> layer.screenPredicate.test(screen)).map(Layer::widget).toList();
        if (widgets.isEmpty()) {
            return;
        }

        var refreshEvents = new BraidEventStream<Unit>();
        var app = new AppState(
            null,
            "BraidLayersBinding",
            MinecraftClient.getInstance(),
            new Surface.Default(),
            new EventBinding.Default(),
            new LayerContext(
                refreshEvents.source(),
                screen,
                new Overlay(
                    new Stack(widgets)
                )
            )
        );

        ((OwoScreenExtension) screen).owo$setBraidLayersState(new LayersState(app, refreshEvents));
    }

    // ---

    public static final Identifier INIT_PHASE = Owo.id("init-braid-layers");

    private static final List<Layer> LAYERS = new ArrayList<>();

    private record Layer(Predicate<Screen> screenPredicate, Widget widget) {}

    @ApiStatus.Internal
    public record LayersState(AppState app, BraidEventStream<Unit> refreshEvents) {}

    // ---

    static {
        NeoForge.EVENT_BUS.<ScreenEvent.Init.Post>addListener(EventPriority.LOW, (event) -> {
            if (((OwoScreenExtension) event.getScreen()).owo$getBraidLayersState() == null) {
                setupLayers(event.getScreen());
            }
        });

        NeoForge.EVENT_BUS.<ScreenEvent.Closing>addListener(EventPriority.LOW, (event) -> {
            var app = ((OwoScreenExtension) event.getScreen()).owo$getBraidLayersApp();
            if (app != null) {
                app.dispose();
            }
        });

        NeoForge.EVENT_BUS.<ScreenEvent.MouseButtonPressed.Pre>addListener(EventPriority.LOW, (event) -> {
            if (tryHandleEvent(event.getScreen(), new MouseButtonPressEvent(event.getButton(), KeyModifiers.NONE))) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.<ScreenEvent.MouseButtonReleased.Pre>addListener(EventPriority.LOW, (event) -> {
            if (tryHandleEvent(event.getScreen(), new MouseButtonReleaseEvent(event.getButton(), KeyModifiers.NONE))) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.<ScreenEvent.MouseScrolled.Pre>addListener(EventPriority.LOW, (event) -> {
            if (tryHandleEvent(event.getScreen(), new MouseScrollEvent(event.getScrollDeltaX(), event.getScrollDeltaY()))) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.<ScreenEvent.KeyPressed.Pre>addListener(EventPriority.LOW, (event) -> {
            if (tryHandleEvent(event.getScreen(), new KeyPressEvent(event.getKeyCode(), event.getScanCode(), event.getModifiers()))) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.<ScreenEvent.KeyReleased.Pre>addListener(EventPriority.LOW, (event) -> {
            if (tryHandleEvent(event.getScreen(), new KeyReleaseEvent(event.getKeyCode(), event.getScanCode(), event.getModifiers()))) {
                event.setCanceled(true);
            }
        });
    }
}
