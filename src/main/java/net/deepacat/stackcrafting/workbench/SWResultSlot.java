package net.deepacat.stackcrafting.workbench;

import net.deepacat.stackcrafting.registry.SCRecipeTypes;
import net.deepacat.stackcrafting.registry.StackedIngredient;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

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

    // TODO: dedup against SWRecipe.matches
    private void consumeItems(Player player, SWRecipe recipe, int baseX, int baseY, boolean mirrored) {
        for (int absX = 0; absX < inputSlots.getWidth(); ++absX) {
            for (int absY = 0; absY < inputSlots.getHeight(); ++absY) {
                int relX = absX - baseX;
                int relY = absY - baseY;
                StackedIngredient ingredient = StackedIngredient.EMPTY;
                if (relX >= 0 && relY >= 0 && relX < recipe.width && relY < recipe.height) {
                    if (mirrored) {
                        ingredient = recipe.recipeItems.get(recipe.width - relX - 1 + relY * recipe.width);
                    } else {
                        ingredient = recipe.recipeItems.get(relX + relY * recipe.width);
                    }
                }
                if (ingredient.isEmpty()) continue;
                int count = ingredient.getItems()[0].getCount();
                int slotIdx = absX + absY * inputSlots.getWidth();
                ItemStack slot = inputSlots.getItem(slotIdx);
                ItemStack junk = slot.getCraftingRemainingItem();
                inputSlots.removeItem(slotIdx, count);
                slot = inputSlots.getItem(slotIdx);
                if (!junk.isEmpty()) {
                    if (slot.isEmpty()) {
                        inputSlots.setItem(slotIdx, junk);
                    } else if (ItemStack.isSameItemSameTags(slot, junk)) {
                        junk.grow(slot.getCount());
                        inputSlots.setItem(slotIdx, junk);
                    } else {
                        player.drop(junk, false);
                    }
                }
            }
        }
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        Level level = player.level();
        Optional<SWRecipe> recipeOpt = level.getRecipeManager().getRecipeFor(SCRecipeTypes.STACK_CRAFTING.get(), inputSlots, level);
        if (recipeOpt.isEmpty()) return;
        SWRecipe recipe = recipeOpt.get();
        // TODO: dedup against SWRecipe.matches
        for (int baseX = 0; baseX <= inputSlots.getWidth() - recipe.width; ++baseX) {
            for (int baseY = 0; baseY <= inputSlots.getHeight() - recipe.height; ++baseY) {
                if (recipe.matches(inputSlots, baseX, baseY, true)) {
                    consumeItems(player, recipe, baseX, baseY, true);
                    return;
                }
                if (recipe.matches(inputSlots, baseX, baseY, false)) {
                    consumeItems(player, recipe, baseX, baseY, false);
                    return;
                }
            }
        }
    }
}
