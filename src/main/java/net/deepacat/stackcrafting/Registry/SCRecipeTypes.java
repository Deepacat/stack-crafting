package net.deepacat.stackcrafting.Registry;

import net.deepacat.stackcrafting.StackCrafting;
import net.deepacat.stackcrafting.workbench.SWRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SCRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, StackCrafting.MODID);

    public static final RegistryObject<RecipeType<SWRecipe>> STACK_CRAFTING = RECIPE_TYPES.register("stack_crafting", () -> registerRecipeType("stack_crafting"));

    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String pIdentifier) {
        return new RecipeType<>() {
            public String toString() {
                return StackCrafting.MODID + ":" + pIdentifier;
            }
        };
    }
}
