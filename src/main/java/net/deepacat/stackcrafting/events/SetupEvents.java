package net.deepacat.stackcrafting.events;

import net.deepacat.stackcrafting.Registry.StackedIngredient;
import net.deepacat.stackcrafting.StackCrafting;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = StackCrafting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupEvents {
    @SubscribeEvent
    public static void registerRecipeElements(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            CraftingHelper.register(new ResourceLocation(StackCrafting.MODID, "stack_crafting"), StackedIngredient.Serializer.INSTANCE);
        }
    }
}
