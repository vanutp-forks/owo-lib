package io.wispforest.owo.util;

import net.minecraft.component.ComponentChanges;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// TODO: pick better name
public interface ViewerStack {
    long count();

    ComponentChanges componentChanges();

    record OfItem(ItemStack stack) implements ViewerStack {
        public static final OfItem EMPTY = new OfItem(ItemStack.EMPTY);

        public static OfItem of(Item item) {
            return new OfItem(new ItemStack(item));
        }

        public static OfItem of(ItemStack stack) {
            return new OfItem(stack);
        }

        public ItemStack asStack() {
            return stack;
        }

        @Override
        public long count() {
            return stack.getCount();
        }

        @Override
        public ComponentChanges componentChanges() {
            return stack.getComponentChanges();
        }
    }

    record OfFluid(Fluid fluid, long count, ComponentChanges componentChanges) implements ViewerStack {}
}
