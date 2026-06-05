package net.enflky.sable_ships.client;

import net.enflky.sable_ships.menu.ModMenuTypes;
import net.enflky.sable_ships.menu.ShipHelmMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;


public final class ClientSetup {

    private ClientSetup() {}

    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SHIP_HELM_MENU.get(),
                (ShipHelmMenu menu, Inventory inv, Component title) ->
                        menu.autoPilot
                                ? new ShipHelmAutoPilotScreen(menu, inv, title)
                                : new ShipHelmScreen(menu, inv, title));
    }

}
