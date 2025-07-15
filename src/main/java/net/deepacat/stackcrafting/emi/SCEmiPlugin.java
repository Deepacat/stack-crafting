package net.deepacat.stackcrafting.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.deepacat.stackcrafting.StackCrafting;
import net.deepacat.stackcrafting.registry.SCBlockRegistry;
import net.deepacat.stackcrafting.registry.SCMenuRegistry;
import net.deepacat.stackcrafting.registry.SCRecipeTypes;
import net.deepacat.stackcrafting.workbench.SWRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class SCEmiPlugin implements EmiPlugin {
    public static final EmiStack STACK_WORKBENCH = EmiStack.of(SCBlockRegistry.STACK_WORKBENCH.get());
    public static final EmiRecipeCategory STACK_CRAFTING = new EmiRecipeCategory(new ResourceLocation(StackCrafting.MODID, "stack_crafting"), STACK_WORKBENCH, new EmiRenderable() {
        @Override
        public void render(GuiGraphics gui, int x, int y, float delta) {
            // TODO: better way to fix this.
            STACK_WORKBENCH.render(gui, x, y, delta);
        }
    });

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(STACK_CRAFTING);
        emiRegistry.addWorkstation(STACK_CRAFTING, STACK_WORKBENCH);
        emiRegistry.addRecipeHandler(SCMenuRegistry.SW_MENU.get(), new SWEmiRecipeHandler());


        RecipeManager manager = emiRegistry.getRecipeManager();

        for (SWRecipe recipe : manager.getAllRecipesFor(SCRecipeTypes.STACK_CRAFTING.get())) {
            emiRegistry.addRecipe(new SWEmiRecipe(recipe));
        }
    }
}
