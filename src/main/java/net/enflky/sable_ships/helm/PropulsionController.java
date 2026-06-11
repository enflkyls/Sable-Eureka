package net.enflky.sable_ships.helm;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

/**
 * Applies linear thrust and yaw torque from pilot input.
 * Scratch buffers are reused across ticks to keep the physics loop allocation-free.
 */
public final class PropulsionController {

    private static final Vector3d GLOBAL_UP = new Vector3d(0.0, 1.0, 0.0);
    private static final double[] FOOTPRINT_SAMPLE_FRACTIONS = {0.0, 0.25, 0.5, 0.75, 1.0};
    private static final double[] BOTTOM_SAMPLE_OFFSETS = {-0.2, 0.2, 0.8, 1.4};

    private final Vector3d worldUp = new Vector3d();
    private final Vector3d defaultForward = new Vector3d();
    private final Vector3d facingWorld = new Vector3d();
    private final Vector3d projFacing = new Vector3d();
    private final Vector3d projDefault = new Vector3d();
    private final Vector3d worldForward = new Vector3d();
    private final Vector3d linearImpulse = new Vector3d();
    private final Vector3d torqueImpulse = new Vector3d();
    private final Vector3d linearVelocity = new Vector3d();
    private final Vector3d horizontalVelocity = new Vector3d();
    private final Vector3d thrustDirection = new Vector3d();
    private final Vector3d horizontalThrustDirection = new Vector3d();
    private final Vector3d velocityCorrection = new Vector3d();
    private final Vector3d zeroAngularVelocity = new Vector3d();
    private final Vector3d sampleLocal = new Vector3d();
    private final Vector3d sampleWorld = new Vector3d();

    public void tick(
            ServerSubLevel subLevel,
            RigidBodyHandle handle,
            Direction blockFacing,
            HelmPhysicsSettings settings,
            HelmInputState input,
            double timeStep,
            double mass
    ) {
        Quaterniondc orientation = subLevel.logicalPose().orientation();

        double scaledThrust = Math.max(0.0, mass * settings.thrustForce() * timeStep);
        double scaledTurn = mass * settings.turnForce() * timeStep;

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
        SurfaceMode surfaceMode = sampleSurfaceMode(subLevel);
        double speedCap = getSpeedCap(surfaceMode, settings);
        enforceHorizontalSpeedCap(handle, speedCap);

        if (input.forward != input.backward && scaledThrust > 0.0 && mass > 0.0) {
            if (speedCap > 0.0) {
                double direction = input.forward ? 1.0 : -1.0;
                handle.getLinearVelocity(linearVelocity);
                horizontalVelocity(linearVelocity, GLOBAL_UP, horizontalVelocity);
                thrustDirection.set(worldForward).mul(direction).normalize();
                horizontalVelocity(thrustDirection, GLOBAL_UP, horizontalThrustDirection);

                double allowedThrust = computeAllowedThrustImpulse(
                        horizontalVelocity,
                        horizontalThrustDirection,
                        speedCap,
                        scaledThrust,
                        mass
                );
                if (allowedThrust > 0.0) {
                    linearImpulse.fma(allowedThrust, thrustDirection);
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

        enforceHorizontalSpeedCap(handle, speedCap);

        if (SableShipsConfig.DEBUG.get()) {
            SableShips.LOGGER.debug("[Prop] facing={} worldForward={} worldUp={} mode={}",
                    blockFacing, worldForward, worldUp, surfaceMode.name);
        }
    }

    private double getSpeedCap(SurfaceMode surfaceMode, HelmPhysicsSettings settings) {
        double cap = surfaceMode == SurfaceMode.WATER ? settings.waterSpeedCap() : settings.landSpeedCap();
        return Math.max(0.0, cap);
    }

    private void horizontalVelocity(Vector3d velocity, Vector3d up, Vector3d dest) {
        dest.set(velocity).fma(-velocity.dot(up), up);
    }

    private void enforceHorizontalSpeedCap(RigidBodyHandle handle, double speedCap) {
        if (speedCap <= 0.0) {
            return;
        }

        handle.getLinearVelocity(linearVelocity);
        double horizontalSpeedSquared = linearVelocity.x * linearVelocity.x + linearVelocity.z * linearVelocity.z;
        double speedCapSquared = speedCap * speedCap;
        if (horizontalSpeedSquared <= speedCapSquared) {
            return;
        }

        double horizontalSpeed = Math.sqrt(horizontalSpeedSquared);
        double scale = speedCap / horizontalSpeed;
        velocityCorrection.set(
                linearVelocity.x * scale - linearVelocity.x,
                0.0,
                linearVelocity.z * scale - linearVelocity.z
        );
        handle.addLinearAndAngularVelocity(velocityCorrection, zeroAngularVelocity.zero());
    }

    private double computeAllowedThrustImpulse(
            Vector3d currentHorizontalVelocity,
            Vector3d requestedDirection,
            double speedCap,
            double requestedImpulse,
            double mass
    ) {
        double requestedDirectionLengthSquared = requestedDirection.lengthSquared();
        if (requestedDirectionLengthSquared <= 1.0E-12 || speedCap <= 0.0 || requestedImpulse <= 0.0 || mass <= 0.0) {
            return 0.0;
        }

        double invDirectionLength = 1.0 / Math.sqrt(requestedDirectionLengthSquared);
        double speedAlongThrust = currentHorizontalVelocity.dot(requestedDirection) * invDirectionLength;
        double horizontalSpeedSquared = currentHorizontalVelocity.lengthSquared();
        double speedCapSquared = speedCap * speedCap;

        if (horizontalSpeedSquared >= speedCapSquared) {
            if (speedAlongThrust >= 0.0) {
                return 0.0;
            }

            double brakingImpulse = mass * -speedAlongThrust;
            return Math.min(requestedImpulse, brakingImpulse);
        }

        double perpendicularSpeedSquared = horizontalSpeedSquared - speedAlongThrust * speedAlongThrust;
        double allowedDeltaSpeedSquared = speedCapSquared - perpendicularSpeedSquared;
        if (allowedDeltaSpeedSquared <= 0.0) {
            return 0.0;
        }

        double allowedDeltaSpeed = Math.sqrt(allowedDeltaSpeedSquared) - speedAlongThrust;
        if (allowedDeltaSpeed <= 0.0) {
            return 0.0;
        }

        return Math.min(requestedImpulse, mass * allowedDeltaSpeed);
    }

    private SurfaceMode sampleSurfaceMode(ServerSubLevel subLevel) {
        BoundingBox3ic bounds = subLevel.getPlot().getBoundingBox();
        double minX = bounds.minX() + 0.5;
        double maxX = bounds.maxX() + 0.5;
        double minZ = bounds.minZ() + 0.5;
        double maxZ = bounds.maxZ() + 0.5;

        for (double yOffset : BOTTOM_SAMPLE_OFFSETS) {
            double sampleY = bounds.minY() + yOffset;
            for (double xFraction : FOOTPRINT_SAMPLE_FRACTIONS) {
                double sampleX = lerp(minX, maxX, xFraction);
                for (double zFraction : FOOTPRINT_SAMPLE_FRACTIONS) {
                    double sampleZ = lerp(minZ, maxZ, zFraction);
                    if (isWaterAt(subLevel, sampleX, sampleY, sampleZ)) {
                        return SurfaceMode.WATER;
                    }
                }
            }
        }

        return SurfaceMode.LAND;
    }

    private boolean isWaterAt(ServerSubLevel subLevel, double x, double y, double z) {
        subLevel.logicalPose().transformPosition(sampleLocal.set(x, y, z), sampleWorld);
        BlockPos worldPos = BlockPos.containing(sampleWorld.x, sampleWorld.y, sampleWorld.z);
        return subLevel.getLevel().getFluidState(worldPos).is(FluidTags.WATER);
    }

    private static double lerp(double min, double max, double fraction) {
        return min + (max - min) * fraction;
    }

    private enum SurfaceMode {
        WATER("water"),
        LAND("land");

        private final String name;

        SurfaceMode(String name) {
            this.name = name;
        }
    }
}
