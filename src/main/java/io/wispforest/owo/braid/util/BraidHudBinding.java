package io.wispforest.owo.braid.util;

import io.wispforest.owo.Owo;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class BraidHudBinding {

    public static void activate(BraidHudElement element) {
        if (Owo.DEBUG && ELEMENTS.contains(element)) {
            throw new IllegalStateException("attempted to activate the same BraidHudElement twice");
        }

        ELEMENTS.add(element);
    }

    private static final List<BraidHudElement> ELEMENTS = new ArrayList<>();

    // ---

    static {
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Post event) -> {
            for (var element : ELEMENTS) {
                element.render(event.getGuiGraphics(), event.getPartialTick());
            }
        });
    }
}
