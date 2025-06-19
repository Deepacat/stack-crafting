package net.deepacat.stackcrafting.workbench;

import net.deepacat.stackcrafting.Registry.SCRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SWResultSlot extends Slot {
    private final CraftingContainer inputSlots;

    public SWResultSlot(CraftingContainer inputSlots, Container container, int id, int x, int y) {
        super(container, id, x, y);
        this.inputSlots = inputSlots;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player player, @NotNull ItemStack stack) {
        Level level = player.level();
        Optional<SWRecipe> recipeOpt = level.getRecipeManager().getRecipeFor(SCRecipeTypes.STACK_CRAFTING.get(), inputSlots, level);
        if (recipeOpt.isEmpty()) return;
        recipeOpt.get().matchAndMaybeConsume(inputSlots, player);
    }
}
