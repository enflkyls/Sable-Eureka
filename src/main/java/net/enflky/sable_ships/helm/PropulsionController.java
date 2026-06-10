package net.enflky.sable_ships.helm;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
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
    private final Vector3d linearVelocity = new Vector3d();
    private final Vector3d sampleLocal = new Vector3d();
    private final Vector3d sampleWorld = new Vector3d();

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

        double scaledThrust = Math.max(0.0, mass * tuning.thrustForce * timeStep);
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
        if (input.forward != input.backward && scaledThrust > 0.0 && mass > 0.0) {
            double speedCap = getSpeedCap(subLevel, tuning);
            if (speedCap > 0.0) {
                double forwardSpeed = handle.getLinearVelocity(linearVelocity).dot(worldForward);
                double direction = input.forward ? 1.0 : -1.0;
                double remainingSpeed = speedCap - (forwardSpeed * direction);

                if (remainingSpeed > 0.0) {
                    double cappedThrust = Math.min(scaledThrust, mass * remainingSpeed);
                    linearImpulse.fma(cappedThrust * direction, worldForward);
                }
            }
        }

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
            SableShips.LOGGER.info("[Prop] facing={} worldForward={} worldUp={} water={}",
                    blockFacing, worldForward, worldUp, isTouchingWater(subLevel));
        }
    }

    private double getSpeedCap(ServerSubLevel subLevel, HelmTuning tuning) {
        double cap = isTouchingWater(subLevel) ? tuning.waterSpeedCap : tuning.landSpeedCap;
        return Math.max(0.0, cap);
    }

    private boolean isTouchingWater(ServerSubLevel subLevel) {
        BoundingBox3ic bounds = subLevel.getPlot().getBoundingBox();
        double minX = bounds.minX() + 0.5;
        double maxX = bounds.maxX() + 0.5;
        double minZ = bounds.minZ() + 0.5;
        double maxZ = bounds.maxZ() + 0.5;
        double centerX = (minX + maxX) * 0.5;
        double centerZ = (minZ + maxZ) * 0.5;
        double sampleY = bounds.minY() + 0.2;

        return isWaterAt(subLevel, centerX, sampleY, centerZ)
                || isWaterAt(subLevel, minX, sampleY, minZ)
                || isWaterAt(subLevel, minX, sampleY, maxZ)
                || isWaterAt(subLevel, maxX, sampleY, minZ)
                || isWaterAt(subLevel, maxX, sampleY, maxZ);
    }

    private boolean isWaterAt(ServerSubLevel subLevel, double x, double y, double z) {
        subLevel.logicalPose().transformPosition(sampleLocal.set(x, y, z), sampleWorld);
        BlockPos worldPos = BlockPos.containing(sampleWorld.x, sampleWorld.y, sampleWorld.z);
        return subLevel.getLevel().getFluidState(worldPos).is(FluidTags.WATER);
    }
}
