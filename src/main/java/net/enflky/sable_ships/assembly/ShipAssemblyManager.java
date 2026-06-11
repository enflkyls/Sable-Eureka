package net.enflky.sable_ships.assembly;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.enflky.sable_ships.helm.ShipOrientation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ShipAssemblyManager {

    private ShipAssemblyManager() {}

    public static void assemble(ServerLevel level, BlockPos helmPos, ServerPlayer player) {
        BlockPos seed = helmPos.below();

        if (!canGather(level.getBlockState(seed), excludedBlockIds())) {
            fail(player, "The block under the helm cannot be assembled.");
            return;
        }

        Set<ResourceLocation> excludedBlocks = excludedBlockIds();
        SubLevelAssemblyHelper.GatherResult gathered = SubLevelAssemblyHelper.gatherConnectedBlocks(
                seed,
                level,
                SableShipsConfig.ASSEMBLY_MAX_BLOCKS.get(),
                (originPos, originState, pos, state, directionFrom) -> canGather(state, excludedBlocks)
        );

        if (gathered.assemblyState() != SubLevelAssemblyHelper.GatherResult.State.SUCCESS
                || gathered.blocks() == null
                || gathered.boundingBox() == null) {
            fail(player, assemblyFailureMessage(gathered));
            return;
        }

        if (!gathered.blocks().contains(helmPos)) {
            fail(player, "The helm must be connected to the ship blocks under it.");
            return;
        }

        SubLevelAssemblyHelper.assembleBlocks(level, seed, gathered.blocks(), gathered.boundingBox());
        success(player, "Ship assembled.");
    }

    public static void disassemble(ServerLevel level, BlockPos helmPos, ServerPlayer player) {
        SubLevelAccess containing = SableCompanion.INSTANCE.getContaining(level, helmPos);
        if (!(containing instanceof ServerSubLevel subLevel)) {
            fail(player, "This helm is not inside an assembled ship.");
            return;
        }

        BoundingBox3ic plotBounds = subLevel.getPlot().getBoundingBox();
        List<BlockPos> blocks = nonAirBlocks(level, plotBounds);
        if (blocks.isEmpty()) {
            fail(player, "This ship has no blocks to disassemble.");
            return;
        }

        BlockPos plotAnchor = subLevel.getPlot().getCenterBlock();
        BlockPos worldAnchor = BlockPos.containing(subLevel.logicalPose().transformPosition(plotAnchor.getCenter()));
        Rotation rotation = nearestRotation(ShipOrientation.computeYaw(subLevel.logicalPose().orientation(), new Vector3d()));
        int angle = angleFor(rotation);

        SubLevelAssemblyHelper.AssemblyTransform transform =
                new SubLevelAssemblyHelper.AssemblyTransform(plotAnchor, worldAnchor, angle, rotation, level);

        SubLevelAssemblyHelper.moveBlocks(level, transform, blocks);
        SubLevelAssemblyHelper.moveTrackingPoints(level, plotBounds, null, transform);
        SubLevelContainer.getContainer(level).removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
        success(player, "Ship disassembled.");
    }

    private static List<BlockPos> nonAirBlocks(ServerLevel level, BoundingBox3ic bounds) {
        List<BlockPos> blocks = new ArrayList<>();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.getBlockState(pos).isAir()) {
                        blocks.add(pos);
                    }
                }
            }
        }
        return blocks;
    }

    private static boolean canGather(BlockState state, Set<ResourceLocation> excludedBlocks) {
        if (!state.getFluidState().isEmpty()) {
            return false;
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return !excludedBlocks.contains(blockId);
    }

    private static Set<ResourceLocation> excludedBlockIds() {
        Set<ResourceLocation> ids = new HashSet<>();
        for (String configuredId : SableShipsConfig.ASSEMBLY_EXCLUDED_BLOCKS.get()) {
            ResourceLocation id = ResourceLocation.tryParse(configuredId);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private static Component assemblyFailureMessage(SubLevelAssemblyHelper.GatherResult result) {
        return switch (result.assemblyState()) {
            case TOO_MANY_BLOCKS -> Component.literal("Ship is too large. Limit: "
                    + SableShipsConfig.ASSEMBLY_MAX_BLOCKS.get() + " blocks.");
            case NO_BLOCKS -> Component.literal("No ship blocks found under the helm.");
            case SUCCESS -> Component.literal("Ship assembly failed.");
        };
    }

    private static Rotation nearestRotation(double yawRadians) {
        double degrees = Math.toDegrees(yawRadians);
        int quadrant = Math.floorMod((int) Math.round(degrees / 90.0), 4);
        return switch (quadrant) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    private static int angleFor(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> 1;
            case CLOCKWISE_180 -> 2;
            case COUNTERCLOCKWISE_90 -> 3;
            default -> 0;
        };
    }

    private static void fail(ServerPlayer player, String message) {
        fail(player, Component.literal(message));
    }

    private static void fail(ServerPlayer player, Component message) {
        player.displayClientMessage(message.copy().withStyle(ChatFormatting.RED), true);
    }

    private static void success(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.GREEN), true);
    }
}
