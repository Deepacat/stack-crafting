package net.deepacat.stackcrafting.Registry;

import net.deepacat.stackcrafting.StackCrafting;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SCItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, StackCrafting.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
