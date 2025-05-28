package net.deepacat.stackcrafting.workbench.recipebook;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.deepacat.stackcrafting.Registry.SCRecipeTypes;
import net.deepacat.stackcrafting.StackCrafting;
import net.deepacat.stackcrafting.workbench.SWRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;

import java.util.function.Supplier;

public class SWRecipeCategory {
    public static final Supplier<RecipeBookCategories> STACK_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("STACK_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> STACK_EQUIPMENT = Suppliers.memoize(() -> RecipeBookCategories.create("STACK_EQUIPMENT", new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD)));
    public static final Supplier<RecipeBookCategories> STACK_BUILDING = Suppliers.memoize(() -> RecipeBookCategories.create("STACK_BUILDING", new ItemStack(Items.BRICK)));
    public static final Supplier<RecipeBookCategories> STACK_MISC = Suppliers.memoize(() -> RecipeBookCategories.create("STACK_MISC", new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE)));
    public static final Supplier<RecipeBookCategories> STACK_REDSTONE = Suppliers.memoize(() -> RecipeBookCategories.create("STACK_REDSTONE", new ItemStack(Items.REDSTONE)));

    public static void init(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(StackCrafting.STACK_CRAFTING_RECIPE, ImmutableList.of(STACK_SEARCH.get(), STACK_EQUIPMENT.get(),
                STACK_BUILDING.get(), STACK_MISC.get(), STACK_REDSTONE.get()));
        event.registerAggregateCategory(STACK_SEARCH.get(), ImmutableList.of(STACK_EQUIPMENT.get(),
                STACK_BUILDING.get(), STACK_MISC.get(), STACK_REDSTONE.get()));
        event.registerRecipeCategoryFinder(SCRecipeTypes.STACK_CRAFTING.get(), recipe -> {
            if (recipe instanceof SWRecipe recipe1) {
                SWRecipeBookTab tab = recipe1.getRecipeBookTab();
                if (tab != null) {
                    return switch (tab) {
                        case EQUIPMENT -> STACK_EQUIPMENT.get();
                        case BUILDING -> STACK_BUILDING.get();
                        case MISC -> STACK_MISC.get();
                        case REDSTONE -> STACK_REDSTONE.get();
                    };
                }
            }
            return STACK_MISC.get();
        });
    }
}
