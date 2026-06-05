package net.enflky.sable_ships.content;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//Prevents the autopilot screen from immediately reopening after the player closes it.
public final class HelmMenuCooldown {

    private static final long COOLDOWN_TICKS = 10L;
    private static final Map<UUID, Long> CLOSE_TIMES = new ConcurrentHashMap<>();

    private HelmMenuCooldown() {}

    public static void markClosed(UUID playerId, long gameTime) {
        CLOSE_TIMES.put(playerId, gameTime);
    }

     //return true when the menu open attempt should be suppressed :>

    public static boolean isOnCooldown(UUID playerId, long gameTime) {
        Long closedAt = CLOSE_TIMES.get(playerId);
        if (closedAt == null) {
            return false;
        }
        if (gameTime - closedAt < COOLDOWN_TICKS) {
            return true;
        }
        CLOSE_TIMES.remove(playerId);
        return false;
    }
}
