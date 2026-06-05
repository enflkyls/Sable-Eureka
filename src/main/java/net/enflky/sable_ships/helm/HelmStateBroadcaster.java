package net.enflky.sable_ships.helm;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.network.HelmStatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaterniondc;
import org.joml.Vector3d;


 //Throttled server-to-client helm state sync Hopely... Avoids broadcasting unchanged snapshots every physics tick to all players.
public final class HelmStateBroadcaster { //why i dontuse it mate i am stupid ?

    private static final double SYNC_RADIUS = 64.0;
    private static final int IDLE_SYNC_INTERVAL = 20;

    private final Vector3d velocityScratch = new Vector3d();
    private final Vector3d yawScratch = new Vector3d();
    private HelmStatePacket lastSent;
    private int ticksSinceSync;


     //DOESNT!! Sends a packet when piloting is active or shittin or when state changed/idle elapsed

    public void tick(
            ServerLevel level,
            BlockPos pos,
            ServerSubLevel subLevel,
            RigidBodyHandle handle,
            HelmTuning tuning,
            HelmInputState input,
            double mass
    ) {
        ticksSinceSync++;

        handle.getLinearVelocity(velocityScratch);
        Quaterniondc orientation = subLevel.logicalPose().orientation();
        double yaw = ShipOrientation.computeYaw(orientation, yawScratch);

        HelmStatePacket current = new HelmStatePacket(
                pos,
                velocityScratch.x, velocityScratch.y, velocityScratch.z,
                yaw,
                mass,
                tuning.thrustForce, tuning.turnForce,
                input.forward, input.backward, input.left, input.right,
                input.piloting,
                Double.MAX_VALUE
        );

        if (!shouldSync(current)) {
            return;
        }

        lastSent = current;
        ticksSinceSync = 0;

        PacketDistributor.sendToPlayersNear(
                level,
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SYNC_RADIUS,
                current
        );
    }

//pilotting requires so much packet
    private boolean shouldSync(HelmStatePacket current) {
        if (current.piloting()) {
            return lastSent == null || !statesEqual(lastSent, current);
        }
        if (ticksSinceSync >= IDLE_SYNC_INTERVAL) {
            return lastSent == null || !statesEqual(lastSent, current);
        }
        return false;
    }

    private static boolean statesEqual(HelmStatePacket a, HelmStatePacket b) {
        return a.piloting() == b.piloting()
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
}
