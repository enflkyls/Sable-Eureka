package net.enflky.sable_ships.client;

import net.enflky.sable_ships.network.HelmStatePacket;
import net.minecraft.core.BlockPos;

public record HelmSnapshot(
        boolean active,
        BlockPos helmPos,
        double velX, double velY, double velZ,
        double speed,
        double yaw,
        double mass,
        double thrustForce,
        double turnForce,
        double distance,
        boolean forward, boolean backward, boolean left, boolean right
) {
    public static final HelmSnapshot EMPTY = new HelmSnapshot(
            false, BlockPos.ZERO,
            0, 0, 0, 0, 0, 0, 0, 0, Double.MAX_VALUE,
            false, false, false, false
    );

    public static HelmSnapshot fromPacket(HelmStatePacket packet) {
        double horizontalSpeed = Math.sqrt(packet.velX() * packet.velX() + packet.velZ() * packet.velZ());
        boolean active = packet.piloting();
        return new HelmSnapshot(
                active,
                packet.pos(),
                packet.velX(), packet.velY(), packet.velZ(),
                horizontalSpeed,
                packet.yaw(),
                packet.mass(),
                packet.thrustForce(), packet.turnForce(),
                packet.distance(),
                packet.fwd(), packet.bwd(), packet.left(), packet.right()
        );
    }
}
