package net.deepacat.stackcrafting.workbench;

import net.deepacat.stackcrafting.Registry.SCRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SWResultSlot extends Slot {
    private final CraftingContainer inputSlots;

    public SWResultSlot(CraftingContainer inputSlots, Container container, int id, int x, int y) {
        super(container, id, x, y);
        this.inputSlots = inputSlots;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        Level level = player.level();
        NonNullList<ItemStack> junkList = level.getRecipeManager().getRemainingItemsFor(
                SCRecipeTypes.STACK_CRAFTING.get(), inputSlots, level);
        for (int i = 0; i < junkList.size(); ++i) {
            ItemStack input = inputSlots.getItem(i);
            ItemStack junk = junkList.get(i);
            if (!input.isEmpty()) {
                inputSlots.removeItem(i, 1);
                input = inputSlots.getItem(i);
            }
            if (!junk.isEmpty()) {
                if (input.isEmpty()) {
                    inputSlots.setItem(i, junk);
                } else if (ItemStack.isSameItemSameTags(input, junk)) {
                    junk.grow(input.getCount());
                    inputSlots.setItem(i, junk);
                } else {
                    player.drop(junk, false);
                }
            }
        }
    }
}
