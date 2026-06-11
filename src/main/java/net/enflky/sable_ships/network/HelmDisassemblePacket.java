package net.enflky.sable_ships.network;

import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.assembly.ShipAssemblyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HelmDisassemblePacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<HelmDisassemblePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SableShips.MOD_ID, "helm_disassemble"));

    public static final StreamCodec<FriendlyByteBuf, HelmDisassemblePacket> STREAM_CODEC =
            StreamCodec.of(HelmDisassemblePacket::encode, HelmDisassemblePacket::decode);

    private static void encode(FriendlyByteBuf buf, HelmDisassemblePacket packet) {
        buf.writeBlockPos(packet.pos);
    }

    private static HelmDisassemblePacket decode(FriendlyByteBuf buf) {
        return new HelmDisassemblePacket(buf.readBlockPos());
    }

    public static void handle(HelmDisassemblePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            if (!HelmAssemblePacket.isValidRequest(serverPlayer, packet.pos)) {
                return;
            }

            ShipAssemblyManager.disassemble(serverPlayer.serverLevel(), packet.pos, serverPlayer);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
