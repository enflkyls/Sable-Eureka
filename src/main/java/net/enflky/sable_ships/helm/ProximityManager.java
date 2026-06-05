package net.enflky.sable_ships.helm;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

/**
 * Throttles helm proximity like limitys for tick for performance idk i done it for performance but like ya itsnt evaluated every render/frame....
 */
public final class ProximityManager {

    public static final double HUD_DISTANCE = 10.0;
    public static final double HUD_DISTANCE_SQR = HUD_DISTANCE * HUD_DISTANCE;
    private static final int CHECK_INTERVAL_TICKS = 10;

    private int ticksSinceCheck;

    public boolean shouldCheck() {
        ticksSinceCheck++;
        if (ticksSinceCheck < CHECK_INTERVAL_TICKS) {
            return false;
        }

        ticksSinceCheck = 0;
        return true;
    }

    public boolean isWithinHudDistance(ServerPlayer player, BlockPos pos) {
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= HUD_DISTANCE_SQR;
    }

    public double distanceTo(ServerPlayer player, BlockPos pos) {
        return Math.sqrt(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
}
