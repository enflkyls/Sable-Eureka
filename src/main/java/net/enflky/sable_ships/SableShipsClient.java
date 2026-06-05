package net.enflky.sable_ships;

import net.enflky.sable_ships.client.ShipHelmOverlay;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SableShips.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SableShips.MOD_ID, value = Dist.CLIENT)
public class SableShipsClient {

    public SableShipsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ShipHelmOverlay());
        SableShips.LOGGER.info("Ships Working in Sable!");
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.SEAT.get(), NoopRenderer::new);
    }

}
