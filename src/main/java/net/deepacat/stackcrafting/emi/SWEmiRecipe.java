package net.deepacat.stackcrafting.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.deepacat.stackcrafting.workbench.SWRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class SWEmiRecipe implements EmiRecipe {
    protected final ResourceLocation id;
    protected final List<EmiIngredient> inputs;
    protected final EmiStack output;

    public SWEmiRecipe(SWRecipe recipe) {
        this.id = recipe.getId();
        this.inputs = padIngredients(recipe);
        this.output = EmiStack.of(recipe.getResultItem());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return SCEmiPlugin.STACK_CRAFTING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    public boolean canFit(int width, int height) {
        if (inputs.size() > 9) {
            return false;
        }
        for (int i = 0; i < inputs.size(); i++) {
            int x = i % 3;
            int y = i / 3;
            if (!inputs.get(i).isEmpty() && (x >= width || y >= height)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
        int sOff = 0;
        if (canFit(1, 3)) {
            sOff -= 1;
        }
        if (canFit(3, 1)) {
            sOff -= 3;
        }
        for (int i = 0; i < 9; i++) {
            int s = i + sOff;
            if (s >= 0 && s < inputs.size()) {
                widgets.addSlot(inputs.get(s), i % 3 * 18, i / 3 * 18);
            } else {
                widgets.addSlot(EmiStack.of(ItemStack.EMPTY), i % 3 * 18, i / 3 * 18);
            }
        }
        widgets.addSlot(output, 92, 14).large(true).recipeContext(this);
    }

    private static List<EmiIngredient> padIngredients(SWRecipe recipe) {
        List<EmiIngredient> list = Lists.newArrayList();
        int i = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x >= recipe.getWidth() || y >= recipe.getHeight() || i >= recipe.getIngredients().size()) {
                    list.add(EmiStack.EMPTY);
                } else {
                    list.add(EmiIngredient.of(recipe.getIngredients().get(i++)));
                }
            }
        }
        return list;
    }
}
