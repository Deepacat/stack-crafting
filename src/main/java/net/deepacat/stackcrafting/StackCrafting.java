package net.deepacat.stackcrafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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

    private static final String PROTOCOL = "1";
    public static final SimpleChannel Network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    public StackCrafting() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::postInit);
        SCBlockRegistry.BLOCKS.register(modBus);
        SCItemRegistry.ITEMS.register(modBus);
    }

    public void postInit(FMLLoadCompleteEvent evt) {

    }
}
