package io.wispforest.owo.ui.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import io.wispforest.owo.Owo;

public final class UISounds {

    public static final SoundEvent UI_INTERACTION = SoundEvent.of(Owo.id("ui.owo.interaction"));

    private UISounds() {}

    @OnlyIn(Dist.CLIENT)
    public static void play(SoundEvent event) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, 1));
    }

    @OnlyIn(Dist.CLIENT)
    public static void playButtonSound() {
        play(SoundEvents.UI_BUTTON_CLICK.value());
    }

    @OnlyIn(Dist.CLIENT)
    public static void playInteractionSound() {
        play(UI_INTERACTION);
    }
}
