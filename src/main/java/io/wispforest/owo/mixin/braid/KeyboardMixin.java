package io.wispforest.owo.mixin.braid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.owo.braid.core.events.CharInputEvent;
import io.wispforest.owo.braid.util.layers.BraidLayersBinding;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @WrapOperation(method = "lambda$charTyped$7", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(CI)Z"))
    private static boolean captureScreenCharTyped(Screen instance, char chr, int modifiers, Operation<Boolean> original) {
        return BraidLayersBinding.tryHandleEvent(instance, new CharInputEvent(chr, modifiers))
            || original.call(instance, chr, modifiers);
    }

    @WrapOperation(method = "lambda$charTyped$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(CI)Z"))
    private static boolean captureScreenCharTyped2(Screen instance, char chr, int modifiers, Operation<Boolean> original) {
        return BraidLayersBinding.tryHandleEvent(instance, new CharInputEvent(chr, modifiers))
            || original.call(instance, chr, modifiers);
    }
}