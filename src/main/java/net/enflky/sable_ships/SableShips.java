package net.enflky.sable_ships;

import net.enflky.sable_ships.client.ClientSetup;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.enflky.sable_ships.menu.ModMenuTypes;
import net.enflky.sable_ships.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(SableShips.MOD_ID)
public class SableShips {

    public static final String MOD_ID = "sable_ships";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SableShips(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SableShipsConfig.SPEC);
        SableShipsBlocks.register(modEventBus);
        SableShipsItems.register(modEventBus);
        SableShipsBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        SableShipsCreativeTabs.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        modEventBus.addListener(ClientSetup::onRegisterScreens);
        modEventBus.addListener(ModNetwork::registerPayloads);
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Ships is working in Sable! :D");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Ships in server working in Sable! :p");
    }
}
