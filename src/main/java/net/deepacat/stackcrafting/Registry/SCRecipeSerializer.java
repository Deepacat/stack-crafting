package net.deepacat.stackcrafting.Registry;

import net.deepacat.stackcrafting.StackCrafting;
import net.deepacat.stackcrafting.workbench.SWRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class SCRecipeSerializer {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StackCrafting.MODID);

    public static final RegistryObject<RecipeSerializer<?>> STACK_CRAFTING = RECIPE_SERIALIZERS.register("stack_crafting", SWRecipe.Serializer::new);
}
