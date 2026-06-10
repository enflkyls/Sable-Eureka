package net.enflky.sable_ships.helm;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import net.minecraft.core.Direction;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

/**
 * Applies linear thrust and yaw torque from pilot input. I will make it with furnace added painnn!!
 * Scratch buffers are reused across ticks to keep the physics loop allocation-free.
 */
public final class PropulsionController {

    private final Vector3d worldUp = new Vector3d();
    private final Vector3d defaultForward = new Vector3d();
    private final Vector3d facingWorld = new Vector3d();
    private final Vector3d projFacing = new Vector3d();
    private final Vector3d projDefault = new Vector3d();
    private final Vector3d worldForward = new Vector3d();
    private final Vector3d linearImpulse = new Vector3d();
    private final Vector3d torqueImpulse = new Vector3d();

    public void tick(
            ServerSubLevel subLevel,
            RigidBodyHandle handle,
            Direction blockFacing,
            HelmTuning tuning,
            HelmInputState input,
            double timeStep,
            double mass
    ) {
        Quaterniondc orientation = subLevel.logicalPose().orientation();

        double scaledThrust = mass * tuning.thrustForce * timeStep; //need to nerf it for wather mate
        double scaledTurn = mass * tuning.turnForce * timeStep; //scales big but fine in bigships

        ShipOrientation.computeWorldForward(
                orientation,
                blockFacing,
                worldUp,
                defaultForward,
                facingWorld,
                projFacing,
                projDefault,
                worldForward
        );

        linearImpulse.zero();
        if (input.forward) linearImpulse.fma(scaledThrust, worldForward);
        if (input.backward) linearImpulse.fma(-scaledThrust, worldForward);

        if (linearImpulse.lengthSquared() > 0.0) {
            handle.applyLinearImpulse(linearImpulse);
        }

        if (input.left || input.right) {
            double torqueAmount = 0;
            if (input.left) torqueAmount += scaledTurn;
            if (input.right) torqueAmount -= scaledTurn;
            handle.applyTorqueImpulse(torqueImpulse.set(worldUp).mul(torqueAmount));
        }

        if (tuning.debug) {
            SableShips.LOGGER.info("[Prop] facing={} worldForward={} worldUp={}",
                    blockFacing, worldForward, worldUp);
        }
    }
}
