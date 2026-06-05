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
 * Client → server pilot input. Flags are packed into a single byte to minimize bandwidth.
 */
public record HelmInputPacket(
        BlockPos pos,
        boolean forward,
        boolean backward,
        boolean left,
        boolean right,
        boolean piloting
) implements CustomPacketPayload {

    public static final double MAX_PILOT_DISTANCE = 10.0;
    private static final double MAX_PILOT_DISTANCE_SQR = MAX_PILOT_DISTANCE * MAX_PILOT_DISTANCE;

    public static final Type<HelmInputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SableShips.MOD_ID, "helm_input"));

    public static final StreamCodec<FriendlyByteBuf, HelmInputPacket> STREAM_CODEC =
            StreamCodec.of(HelmInputPacket::encode, HelmInputPacket::decode);

    private static void encode(FriendlyByteBuf buf, HelmInputPacket packet) {
        buf.writeBlockPos(packet.pos);
        byte flags = 0;
        if (packet.forward) flags |= 1;
        if (packet.backward) flags |= 2;
        if (packet.left) flags |= 4;
        if (packet.right) flags |= 8;
        if (packet.piloting) flags |= 16;
        buf.writeByte(flags);
    }

    private static HelmInputPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        byte flags = buf.readByte();
        return new HelmInputPacket(
                pos,
                (flags & 1) != 0,
                (flags & 2) != 0,
                (flags & 4) != 0,
                (flags & 8) != 0,
                (flags & 16) != 0
        );
    }

    public static void handle(HelmInputPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            if (serverPlayer.distanceToSqr(
                    packet.pos.getX() + 0.5,
                    packet.pos.getY() + 0.5,
                    packet.pos.getZ() + 0.5) > MAX_PILOT_DISTANCE_SQR) {
                return;
            }

            BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.pos);
            if (!(blockEntity instanceof ShipHelmBlockEntity helm)) {
                return;
            }

            helm.input().setFromPlayer(serverPlayer, packet.forward(), packet.backward(), packet.left(), packet.right(), packet.piloting());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
