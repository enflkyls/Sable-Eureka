package net.enflky.sable_ships.network;

import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.content.ShipHelmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → server tuning update when the player adjusts helm sliders.
 */
public record HelmSettingsPacket(
        BlockPos pos,
        double thrustForce,
        double turnForce,
        double kp,
        double kd,
        double ki
) implements CustomPacketPayload {

    public static final Type<HelmSettingsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SableShips.MOD_ID, "helm_settings"));

    public static final StreamCodec<FriendlyByteBuf, HelmSettingsPacket> STREAM_CODEC =
            StreamCodec.of(HelmSettingsPacket::encode, HelmSettingsPacket::decode);

    private static void encode(FriendlyByteBuf buf, HelmSettingsPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeDouble(packet.thrustForce);
        buf.writeDouble(packet.turnForce);
        buf.writeDouble(packet.kp);
        buf.writeDouble(packet.kd);
        buf.writeDouble(packet.ki);
    }

    private static HelmSettingsPacket decode(FriendlyByteBuf buf) {
        return new HelmSettingsPacket(
                buf.readBlockPos(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
    }

    public static void handle(HelmSettingsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            if (serverPlayer.distanceToSqr(
                    packet.pos.getX() + 0.5,
                    packet.pos.getY() + 0.5,
                    packet.pos.getZ() + 0.5) > 64.0) {
                return;
            }

            BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.pos);
            if (!(blockEntity instanceof ShipHelmBlockEntity helm)) {
                return;
            }
//this iscommentted beacyse there can be vulnabilitiry in here soooooooookay fixing
//            helm.tuning().applyFromClient(
//                    packet.thrustForce(), packet.turnForce(),
//                    packet.kp(), packet.kd(), packet.ki()
//            );
            helm.setChanged();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
