package net.enflky.sable_ships.helm;

import net.enflky.sable_ships.config.SableShipsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public final class ProximityManager {

    private int ticksSinceCheck;

    public boolean shouldCheck() {
        ticksSinceCheck++;
        if (ticksSinceCheck < SableShipsConfig.PROXIMITY_CHECK_INTERVAL_TICKS.get()) {
            return false;
        }

        ticksSinceCheck = 0;
        return true;
    }

    public boolean isWithinHudDistance(ServerPlayer player, BlockPos pos) {
        double hudDistance = SableShipsConfig.HUD_DISTANCE_BLOCKS.get();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= hudDistance * hudDistance;
    }

    public double distanceTo(ServerPlayer player, BlockPos pos) {
        return Math.sqrt(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
}
