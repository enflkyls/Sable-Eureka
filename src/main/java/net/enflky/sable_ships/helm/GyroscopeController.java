package net.enflky.sable_ships.helm;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.config.SableShipsConfig;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class GyroscopeController {

    private static final Vector3d WORLD_UP = new Vector3d(0.0, 1.0, 0.0);

    private final Vector3d currentUpWorld = new Vector3d();
    private final Vector3d errorAxisWorld = new Vector3d();
    private final Vector3d errorAxisLocal = new Vector3d();
    private final Vector3d angularVelocityWorld = new Vector3d();
    private final Vector3d angularVelocityLocal = new Vector3d();
    private final Vector3d restoringImpulseLocal = new Vector3d();
    private final Vector3d dampingImpulseLocal = new Vector3d();
    private final Vector3d integralLocal = new Vector3d();
    private final Vector3d torqueScratch = new Vector3d();

    public void tick(ServerSubLevel subLevel, RigidBodyHandle handle, HelmPhysicsSettings settings,
                     double timeStep, double mass) {
        double strengthImpulse = mass * settings.stabilizationStrength();
        double dampingImpulse = mass * settings.stabilizationDamping();
        double correctionImpulse = mass * settings.stabilizationCorrection();

        if (strengthImpulse <= 0.0 && dampingImpulse <= 0.0 && correctionImpulse <= 0.0) {
            integralLocal.zero();
            return;
        }

        Quaterniondc orientation = subLevel.logicalPose().orientation();

        handle.getAngularVelocity(angularVelocityWorld);
        orientation.transformInverse(angularVelocityWorld, angularVelocityLocal);

        orientation.transform(WORLD_UP, currentUpWorld);
        currentUpWorld.cross(WORLD_UP, errorAxisWorld);
        orientation.transformInverse(errorAxisWorld, errorAxisLocal);

        integralLocal.fma(timeStep, errorAxisLocal);
        // Strength pulls the ship upright (finally) and correction slowly removes lingering lean
        restoringImpulseLocal
                .set(errorAxisLocal).mul(strengthImpulse * timeStep)
                .fma(correctionImpulse * timeStep, integralLocal);

        // Damping resists wobble by pushing against current angular velocity some guy said what '_'
        dampingImpulseLocal.set(angularVelocityLocal).mul(-dampingImpulse * timeStep);
        dampingImpulseLocal.mul(clampingFactor(angularVelocityLocal, dampingImpulseLocal));

        torqueScratch.set(restoringImpulseLocal).add(dampingImpulseLocal);
        handle.applyTorqueImpulse(torqueScratch);

        if (SableShipsConfig.DEBUG.get()) {
            SableShips.LOGGER.error("[Gyro] errorAxisLocal:       {}", errorAxisLocal);
            SableShips.LOGGER.error("[Gyro] angularVelocityLocal: {}", angularVelocityLocal);
            SableShips.LOGGER.error("[Gyro] totalImpulse:         {}", torqueScratch);
        }
    }
    private static double clampingFactor(Vector3dc currentVelocity, Vector3dc expectedVelocityChange) {
        double k = -currentVelocity.dot(expectedVelocityChange);
        double v = currentVelocity.lengthSquared();
        if (k < 0.0) {
            return 0.0;
        }
        if (10.0 * k < v) {
            return 1.0 - k / (2.0 * v);
        }
        return v < 1.0E-10 ? v / (k + 1.0E-10) : v * (1.0 - Math.exp(-k / v)) / k;
    }
}
