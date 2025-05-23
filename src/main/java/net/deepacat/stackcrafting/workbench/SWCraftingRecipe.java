package net.deepacat.stackcrafting.workbench;

import net.deepacat.stackcrafting.Registry.SCRecipeTypes;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface SWCraftingRecipe extends Recipe<CraftingContainer> {
    default RecipeType<?> getType() {
        return SCRecipeTypes.STACK_CRAFTING.get();
    }

    CraftingBookCategory category();
}
