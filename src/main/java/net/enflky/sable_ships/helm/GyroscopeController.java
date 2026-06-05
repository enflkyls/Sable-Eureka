package net.enflky.sable_ships.helm;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

//Gyroscope that stabiles the ship
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

    public void tick(ServerSubLevel subLevel, RigidBodyHandle handle, HelmTuning tuning,
                     double timeStep, double mass) {
        double skp = mass * tuning.kp;
        double skd = mass * tuning.kd;
        double ski = mass * tuning.ki;

        if (skp <= 0.0 && skd <= 0.0 && ski <= 0.0) {
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
        restoringImpulseLocal
                .set(errorAxisLocal).mul(skp * timeStep)
                .fma(ski * timeStep, integralLocal);

        dampingImpulseLocal.set(angularVelocityLocal).mul(-skd * timeStep);
        dampingImpulseLocal.mul(clampingFactor(angularVelocityLocal, dampingImpulseLocal));

        torqueScratch.set(restoringImpulseLocal).add(dampingImpulseLocal);
        handle.applyTorqueImpulse(torqueScratch);

        if (tuning.debug) {
            SableShips.LOGGER.error("[Gyro] errorAxisLocal:       {}", errorAxisLocal);
            SableShips.LOGGER.error("[Gyro] angularVelocityLocal: {}", angularVelocityLocal);
            SableShips.LOGGER.error("[Gyro] totalImpulse:         {}", torqueScratch);
        }
    }
    //Some shitty maths
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
