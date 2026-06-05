package net.enflky.sable_ships.network;

import net.enflky.sable_ships.SableShips;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Central registration for all helm network payloads on the mod event bus.
 */
public final class ModNetwork {

    private ModNetwork() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SableShips.MOD_ID);

        registrar.playToClient(
                HelmStatePacket.TYPE,
                HelmStatePacket.CODEC,
                HelmStatePacket::handle
        );

        registrar.playToServer(
                HelmInputPacket.TYPE,
                HelmInputPacket.STREAM_CODEC,
                HelmInputPacket::handle
        );

        registrar.playToServer(
                HelmSettingsPacket.TYPE,
                HelmSettingsPacket.STREAM_CODEC,
                HelmSettingsPacket::handle
        );
    }
}
