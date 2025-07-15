package net.deepacat.stackcrafting;

import net.deepacat.stackcrafting.registry.*;
import net.deepacat.stackcrafting.workbench.SWScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(StackCrafting.MODID)
public class StackCrafting {
    public static final String MODID = "stackcrafting";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final RecipeBookType STACK_CRAFTING_RECIPE = RecipeBookType.create("STACK_CRAFTING");

    private static final String PROTOCOL = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    public StackCrafting() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::postInit);
        SCBlockRegistry.BLOCKS.register(modEventBus);
        SCItemRegistry.ITEMS.register(modEventBus);
        SCMenuRegistry.register(modEventBus);
        SCRecipeTypes.RECIPE_TYPES.register(modEventBus);
        SCRecipeSerializer.RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        MenuScreens.register(SCMenuRegistry.SW_MENU.get(), SWScreen::new);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(SCBlockRegistry.STACK_WORKBENCH);
        }
    }

    public void postInit(FMLLoadCompleteEvent evt) {

    }
}
