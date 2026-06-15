package io.wispforest.owo.compat.rei;

import io.wispforest.owo.util.ViewerStack;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

public class ReiStackUtil {
    public static ViewerStack fromRei(EntryStack<?> stack) {
        if (stack.getValue() instanceof ItemStack item) {
            return ViewerStack.OfItem.of(item);
        } else {
            // TODO: fluid entries arrive as architectury FluidStack, which is not yarn-mapped
            //  under NeoForge here, so they (and custom REI stacks) cannot be unpacked.
            return ViewerStack.OfItem.EMPTY;
        }
    }

    public static EntryStack<?> toRei(ViewerStack stack) {
        if (stack instanceof ViewerStack.OfItem ofItem) {
            return EntryStacks.of(ofItem.asStack());
        } else if (stack instanceof ViewerStack.OfFluid ofFluid) {
            return EntryStacks.of(ofFluid.fluid(), ofFluid.count());
        } else {
            throw new IllegalStateException("Invalid ViewerStack");
        }
    }
}
