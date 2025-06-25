package net.deepacat.stackcrafting.workbench;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.deepacat.stackcrafting.registry.SCRecipeSerializer;
import net.deepacat.stackcrafting.registry.SCRecipeTypes;
import net.deepacat.stackcrafting.registry.StackedIngredient;
import net.deepacat.stackcrafting.StackCrafting;
import net.deepacat.stackcrafting.workbench.recipebook.SWRecipeBookTab;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SWRecipe implements IShapedRecipe<CraftingContainer> {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    final int width;
    final int height;
    final NonNullList<StackedIngredient> recipeItems;
    final ItemStack result;
    private final ResourceLocation id;
    final String group;
    final SWRecipeBookTab category;

    public SWRecipe(ResourceLocation pId, String pGroup, SWRecipeBookTab pCategory, int pWidth, int pHeight,
                    NonNullList<StackedIngredient> pRecipeItems, ItemStack pResult) {
        this.id = pId;
        this.group = pGroup;
        this.category = pCategory;
        this.width = pWidth;
        this.height = pHeight;
        this.recipeItems = pRecipeItems;
        this.result = pResult;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return SCRecipeSerializer.STACK_CRAFTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SCRecipeTypes.STACK_CRAFTING.get();
    }

    public String getGroup() {
        return this.group;
    }

    public @NotNull ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
    }

    public @NotNull ItemStack getResultItem() {
        return result;
    }

    @Nullable
    public SWRecipeBookTab getRecipeBookTab() {
        return this.category;
    }

    @SuppressWarnings("unchecked")
    public NonNullList<Ingredient> getIngredients() {
        return (NonNullList<Ingredient>) (NonNullList<?>) this.recipeItems;
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.width && pHeight >= this.height;
    }

    public boolean matches(CraftingContainer inv, Level level) {
        for (int baseX = 0; baseX <= inv.getWidth() - this.width; ++baseX) {
            for (int baseY = 0; baseY <= inv.getHeight() - this.height; ++baseY) {
                if (this.matches(inv, baseX, baseY, true)) {
                    return true;
                }

                if (this.matches(inv, baseX, baseY, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean matches(CraftingContainer pCraftingInventory, int baseX, int baseY, boolean mirrored) {
        for (int absX = 0; absX < pCraftingInventory.getWidth(); ++absX) {
            for (int absY = 0; absY < pCraftingInventory.getHeight(); ++absY) {
                int relX = absX - baseX;
                int relY = absY - baseY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (relX >= 0 && relY >= 0 && relX < this.width && relY < this.height) {
                    if (mirrored) {
                        ingredient = recipeItems.get(this.width - relX - 1 + relY * this.width);
                    } else {
                        ingredient = recipeItems.get(relX + relY * this.width);
                    }
                }
                if (!ingredient.test(pCraftingInventory.getItem(absX + absY * pCraftingInventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return this.getResultItem(pRegistryAccess).copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getRecipeWidth() {
        return this.getWidth();
    }

    public int getHeight() {
        return this.height;
    }

    public int getRecipeHeight() {
        return this.getHeight();
    }

    static NonNullList<StackedIngredient> dissolvePattern(String[] pattern, Map<String, StackedIngredient> keyMap, int width, int height) {
        NonNullList<StackedIngredient> result = NonNullList.withSize(width * height, StackedIngredient.EMPTY);
        Set<String> unusedKeys = Sets.newHashSet(keyMap.keySet());
        unusedKeys.remove(" ");
        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String key = pattern[i].substring(j, j + 1);
                StackedIngredient ingredient = keyMap.get(key);
                if (ingredient == null)
                    throw new JsonSyntaxException("Pattern references symbol '" + key + "' but it's not defined in the key");
                unusedKeys.remove(key);
                result.set(j + width * i, ingredient);
            }
        }
        if (!unusedKeys.isEmpty())
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + unusedKeys);
        return result;
    }

    @VisibleForTesting
    static String[] shrink(String... pToShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < pToShrink.length; ++i1) {
            String s = pToShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (pToShrink.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[pToShrink.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = pToShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().filter((empty) -> {
            return !empty.isEmpty();
        }).anyMatch((ingredient) -> {
            return ForgeHooks.hasNoElements(ingredient);
        });
    }

    private static int firstNonSpace(String pEntry) {
        int i;
        for (i = 0; i < pEntry.length() && pEntry.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String pEntry) {
        int i;
        for (i = pEntry.length() - 1; i >= 0 && pEntry.charAt(i) == ' '; --i) {
        }

        return i;
    }

    static String[] patternFromJson(JsonArray pPatternArray) {
        String[] astring = new String[pPatternArray.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(pPatternArray.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    static Map<String, StackedIngredient> keyFromJson(JsonObject keyJson) {
        Map<String, StackedIngredient> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : keyJson.entrySet()) {
            if ((entry.getKey()).length() != 1)
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            if (" ".equals(entry.getKey()))
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            // TODO: validate non-empty.
            map.put(entry.getKey(), StackedIngredient.Serializer.INSTANCE.parse((JsonObject) entry.getValue()));
        }

        map.put(" ", StackedIngredient.EMPTY);
        return map;
    }

    public static ItemStack itemStackFromJson(JsonObject pStackObject) {
        return CraftingHelper.getItemStack(pStackObject, true, true);
    }

    public static class Serializer implements RecipeSerializer<SWRecipe> {
        private static final ResourceLocation NAME = new ResourceLocation(StackCrafting.MODID, "stack_crafting");

        public Serializer() {
        }

        public SWRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            final String categoryKeyIn = GsonHelper.getAsString(pJson, "category", (String) null);
            final SWRecipeBookTab keyIn = SWRecipeBookTab.findByName(categoryKeyIn);
            Map<String, StackedIngredient> map = keyFromJson(GsonHelper.getAsJsonObject(pJson, "key"));
            String[] astring = shrink(patternFromJson(GsonHelper.getAsJsonArray(pJson, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<StackedIngredient> nonnulllist = dissolvePattern(astring, map, i, j);
            ItemStack itemstack = itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            return new SWRecipe(pRecipeId, s, keyIn, i, j, nonnulllist, itemstack);
        }

        public SWRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            int j = pBuffer.readVarInt();
            String s = pBuffer.readUtf();
            SWRecipeBookTab craftingbookcategory = SWRecipeBookTab.findByName(pBuffer.readUtf());
            NonNullList<StackedIngredient> nonnulllist = NonNullList.withSize(i * j, StackedIngredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, StackedIngredient.Serializer.INSTANCE.parse(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItem();
            return new SWRecipe(pRecipeId, s, craftingbookcategory, i, j, nonnulllist, itemstack);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, SWRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.width);
            pBuffer.writeVarInt(pRecipe.height);
            pBuffer.writeUtf(pRecipe.group);
            pBuffer.writeEnum(pRecipe.category);
            Iterator var3 = pRecipe.recipeItems.iterator();

            while (var3.hasNext()) {
                Ingredient ingredient = (Ingredient) var3.next();
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.result);
        }
    }
}
