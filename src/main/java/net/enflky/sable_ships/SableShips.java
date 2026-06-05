package net.enflky.sable_ships;

import net.enflky.sable_ships.client.ClientSetup;
import net.enflky.sable_ships.menu.ModMenuTypes;
import net.enflky.sable_ships.network.ModNetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(SableShips.MOD_ID)
public class SableShips {

    public static final String MOD_ID = "sable_ships";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean DEBUG = false;

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "sable_ships");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            CREATIVE_MODE_TABS.register("main", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.sable_ships"))
                            .icon(() -> SableShipsBlocks.SHIP_HELMS.get("oak").get().asItem().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                SableShipsBlocks.SHIP_HELMS.values().forEach(h -> output.accept(h.get()));
                                SableShipsBlocks.SEATS.values().forEach(s -> output.accept(s.get()));
                            })
                            .build());

    public SableShips(IEventBus modEventBus, ModContainer modContainer) {
        SableShipsBlocks.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
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
