package net.enflky.sable_ships.helm;

import net.enflky.sable_ships.network.HelmStatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-authed based pilot state syncretization with throttled proximity checks (Finaly no more f***ckin dissappreas)
 */
public final class PilotStateManager {

    private static final double TELEMETRY_RADIUS = 64.0;
    private static final int IDLE_SYNC_INTERVAL_TICKS = 20;

    private final Map<UUID, PlayerSync> lastSentByPlayer = new HashMap<>();
    private final ProximityManager proximityManager = new ProximityManager();
    private int idleTicks;

    public void tick(
            ServerLevel level,
            BlockPos helmPos,
            HelmInputState input,
            Vector3d velocity,
            double yaw,
            double mass,
            HelmTuning tuning
    ) {
        idleTicks++;

        ServerPlayer pilot = input.pilotId == null ? null : level.getServer().getPlayerList().getPlayer(input.pilotId);
        boolean proximityDue = proximityManager.shouldCheck();

        if (input.piloting && (pilot == null || (proximityDue && !proximityManager.isWithinHudDistance(pilot, helmPos)))) {
            input.clear();
            pilot = null;
        }

        for (ServerPlayer player : level.players()) {
            double distance = proximityManager.distanceTo(player, helmPos);
            if (distance > TELEMETRY_RADIUS) {
                lastSentByPlayer.remove(player.getUUID());
                continue;
            }

            boolean isPilot = pilot != null && player.getUUID().equals(pilot.getUUID()) && input.piloting;
            boolean visible = isPilot && proximityManager.isWithinHudDistance(player, helmPos);
            HelmStatePacket packet = new HelmStatePacket(
                    helmPos,
                    velocity.x, velocity.y, velocity.z,
                    yaw,
                    mass,
                    tuning.thrustForce, tuning.turnForce,
                    input.forward, input.backward, input.left, input.right,
                    visible,
                    distance
            );

            if (shouldSync(player.getUUID(), packet, visible)) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }

        if (idleTicks >= IDLE_SYNC_INTERVAL_TICKS) {
            idleTicks = 0;
        }
    }

    private boolean shouldSync(UUID playerId, HelmStatePacket packet, boolean visible) {
        PlayerSync previous = lastSentByPlayer.get(playerId);
        boolean due = visible || idleTicks >= IDLE_SYNC_INTERVAL_TICKS;
        if (!due && previous != null && !previous.packet.piloting()) {
            return false;
        }
        if (previous != null && statesEqual(previous.packet, packet)) {
            return false;
        }

        lastSentByPlayer.put(playerId, new PlayerSync(packet));
        return true;
    }

    private static boolean statesEqual(HelmStatePacket a, HelmStatePacket b) {
        return a.piloting() == b.piloting()
                && a.pos().equals(b.pos())
                && a.fwd() == b.fwd() && a.bwd() == b.bwd()
                && a.left() == b.left() && a.right() == b.right()
                && Double.compare(a.thrustForce(), b.thrustForce()) == 0
                && Double.compare(a.turnForce(), b.turnForce()) == 0
                && approxEqual(a.velX(), b.velX()) && approxEqual(a.velY(), b.velY())
                && approxEqual(a.velZ(), b.velZ())
                && approxEqual(a.yaw(), b.yaw())
                && approxEqual(a.mass(), b.mass())
                && approxEqual(a.distance(), b.distance());
    }

    private static boolean approxEqual(double a, double b) {
        return Math.abs(a - b) < 0.01;
    }

    private record PlayerSync(HelmStatePacket packet) {
    }
}
