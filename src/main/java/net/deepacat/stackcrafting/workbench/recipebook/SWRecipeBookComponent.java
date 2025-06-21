package net.deepacat.stackcrafting.workbench.recipebook;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SWRecipeBookComponent extends RecipeBookComponent {
    @Override
    protected void updateCollections(boolean reset) {
        // TODO: some checks aren't necessary if grid is always 3x3.
        List<RecipeCollection> collections = book.getCollection(selectedTab.getCategory());
        for (RecipeCollection collection : collections) {
            for (Recipe<?> recipe : collection.recipes) {
                boolean fits = recipe.canCraftInDimensions(menu.getGridWidth(), menu.getGridHeight());
                if (fits) {
                    collection.fitsDimensions.add(recipe);
                } else {
                    collection.fitsDimensions.remove(recipe);
                }
                if (fits && stackedContents.canCraft(recipe, null)) {
                    collection.craftable.add(recipe);
                } else {
                    collection.craftable.remove(recipe);
                }
            }
        }
        collections = new ArrayList<>(collections);
        collections.removeIf(x -> !x.hasFitting());
        String text = searchBox.getValue();
        if (!text.isEmpty()) {
            ObjectSet<RecipeCollection> matches = new ObjectLinkedOpenHashSet<>(
                    minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(text.toLowerCase(Locale.ROOT)));
            collections.removeIf(x -> !matches.contains(x));
        }
        if (book.isFiltering(menu)) {
            collections.removeIf(x -> !x.hasCraftable());
        }
        recipeBookPage.updateCollections(collections, reset);
    }
}
