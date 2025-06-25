package net.deepacat.stackcrafting.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.deepacat.stackcrafting.StackCrafting;
import net.deepacat.stackcrafting.registry.SCBlockRegistry;
import net.deepacat.stackcrafting.registry.SCRecipeTypes;
import net.deepacat.stackcrafting.workbench.SWRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class SCEmiPlugin implements EmiPlugin {
    public static final EmiStack STACK_WORKBENCH = EmiStack.of(SCBlockRegistry.STACK_WORKBENCH.get());
    public static final EmiRecipeCategory STACK_CRAFTING = new EmiRecipeCategory(new ResourceLocation(StackCrafting.MODID, "stack_crafting"), STACK_WORKBENCH, EmiTexture.PLUS);

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(STACK_CRAFTING);
        emiRegistry.addWorkstation(STACK_CRAFTING, STACK_WORKBENCH);

        RecipeManager manager = emiRegistry.getRecipeManager();

        for (SWRecipe recipe : manager.getAllRecipesFor(SCRecipeTypes.STACK_CRAFTING.get())) {
            emiRegistry.addRecipe(new SWEmiRecipe(recipe));
        }
    }
}
