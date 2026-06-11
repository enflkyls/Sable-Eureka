package net.enflky.sable_ships.network;

import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.assembly.ShipAssemblyManager;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.enflky.sable_ships.content.ShipHelmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HelmAssemblePacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<HelmAssemblePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SableShips.MOD_ID, "helm_assemble"));

    public static final StreamCodec<FriendlyByteBuf, HelmAssemblePacket> STREAM_CODEC =
            StreamCodec.of(HelmAssemblePacket::encode, HelmAssemblePacket::decode);

    private static void encode(FriendlyByteBuf buf, HelmAssemblePacket packet) {
        buf.writeBlockPos(packet.pos);
    }

    private static HelmAssemblePacket decode(FriendlyByteBuf buf) {
        return new HelmAssemblePacket(buf.readBlockPos());
    }

    public static void handle(HelmAssemblePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            if (!isValidRequest(serverPlayer, packet.pos)) {
                return;
            }

            ShipAssemblyManager.assemble(serverPlayer.serverLevel(), packet.pos, serverPlayer);
        });
    }

    static boolean isValidRequest(ServerPlayer serverPlayer, BlockPos pos) {
        double maxDistance = SableShipsConfig.HUD_DISTANCE_BLOCKS.get();
        if (serverPlayer.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5) > maxDistance * maxDistance) {
            return false;
        }

        BlockEntity blockEntity = serverPlayer.level().getBlockEntity(pos);
        return blockEntity instanceof ShipHelmBlockEntity;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
