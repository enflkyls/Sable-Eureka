package net.enflky.sable_ships.network;

import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.client.ClientHelmState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → client helm telemetry for HUD and debug screens.
 */
public record HelmStatePacket(
        BlockPos pos,
        double velX, double velY, double velZ,
        double yaw,
        double mass,
        double thrustForce, double turnForce,
        boolean fwd, boolean bwd, boolean left, boolean right,
        boolean piloting,
        double distance
) implements CustomPacketPayload {

    public static final Type<HelmStatePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SableShips.MOD_ID, "helm_state"));

    public static final StreamCodec<FriendlyByteBuf, HelmStatePacket> CODEC =
            StreamCodec.of(HelmStatePacket::encode, HelmStatePacket::decode);

    private static void encode(FriendlyByteBuf buf, HelmStatePacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeDouble(packet.velX);
        buf.writeDouble(packet.velY);
        buf.writeDouble(packet.velZ);
        buf.writeDouble(packet.yaw);
        buf.writeDouble(packet.mass);
        buf.writeDouble(packet.thrustForce);
        buf.writeDouble(packet.turnForce);
        buf.writeBoolean(packet.fwd);
        buf.writeBoolean(packet.bwd);
        buf.writeBoolean(packet.left);
        buf.writeBoolean(packet.right);
        buf.writeBoolean(packet.piloting);
        buf.writeDouble(packet.distance);
    }

    private static HelmStatePacket decode(FriendlyByteBuf buf) {
        return new HelmStatePacket(
                buf.readBlockPos(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(), buf.readDouble(),
                buf.readBoolean(), buf.readBoolean(),
                buf.readBoolean(), buf.readBoolean(),
                buf.readBoolean(),
                buf.readDouble()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HelmStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHelmState.update(packet));
    }
}
