package io.wispforest.owo.mixin.braid;

import io.wispforest.owo.braid.core.events.CharInputEvent;
import io.wispforest.owo.braid.util.layers.BraidLayersBinding;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "method_1473", at = @At("HEAD"), cancellable = true)
    private static void captureScreenCharTyped(Element element, char character, int modifiers, CallbackInfo ci) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (BraidLayersBinding.tryHandleEvent(screen, new CharInputEvent(character, modifiers))) {
            ci.cancel();
        }
    }

    @Inject(method = "method_1458", at = @At("HEAD"), cancellable = true)
    private static void captureScreenCharTyped2(Element element, int character, int modifiers, CallbackInfo ci) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (BraidLayersBinding.tryHandleEvent(screen, new CharInputEvent((char) character, modifiers))) {
            ci.cancel();
        }
    }
}
