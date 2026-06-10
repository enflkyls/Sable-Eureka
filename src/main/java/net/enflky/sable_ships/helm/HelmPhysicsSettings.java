package net.enflky.sable_ships.helm;

import net.enflky.sable_ships.config.SableShipsConfig;

public record HelmPhysicsSettings(
        double stabilizationStrength,
        double stabilizationDamping,
        double stabilizationCorrection,
        double thrustForce,
        double turnForce,
        double waterSpeedCap,
        double landSpeedCap
) {
    public static HelmPhysicsSettings fromConfig(int activeEngines) {
        return new HelmPhysicsSettings(
                SableShipsConfig.GYRO_STRENGTH.get(),
                SableShipsConfig.GYRO_DAMPING.get(),
                SableShipsConfig.GYRO_CORRECTION.get(),
                SableShipsConfig.HELM_BASE_THRUST.get()
                        + activeEngines * SableShipsConfig.SHIP_ENGINE_THRUST_BONUS.get(),
                SableShipsConfig.HELM_TURN_FORCE.get(),
                SableShipsConfig.HELM_WATER_SPEED_CAP.get()
                        + activeEngines * SableShipsConfig.SHIP_ENGINE_WATER_SPEED_CAP_BONUS.get(),
                SableShipsConfig.HELM_LAND_SPEED_CAP.get()
        );
    }
}
