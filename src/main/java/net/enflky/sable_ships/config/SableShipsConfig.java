package net.enflky.sable_ships.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SableShipsConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue HELM_BASE_THRUST;
    public static final ModConfigSpec.DoubleValue HELM_TURN_FORCE;
    public static final ModConfigSpec.DoubleValue HELM_WATER_SPEED_CAP;
    public static final ModConfigSpec.DoubleValue HELM_LAND_SPEED_CAP;
    public static final ModConfigSpec.DoubleValue SHIP_ENGINE_THRUST_BONUS;
    public static final ModConfigSpec.DoubleValue SHIP_ENGINE_WATER_SPEED_CAP_BONUS;
    public static final ModConfigSpec.IntValue SHIP_ENGINE_MAX_STACKING_ENGINES;
    public static final ModConfigSpec.IntValue SHIP_ENGINE_SCAN_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue HUD_DISTANCE_BLOCKS;
    public static final ModConfigSpec.DoubleValue TELEMETRY_RADIUS;
    public static final ModConfigSpec.IntValue IDLE_SYNC_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue PROXIMITY_CHECK_INTERVAL_TICKS;
    public static final ModConfigSpec.DoubleValue GYRO_STRENGTH;
    public static final ModConfigSpec.DoubleValue GYRO_DAMPING;
    public static final ModConfigSpec.DoubleValue GYRO_CORRECTION;
    public static final ModConfigSpec.BooleanValue DEBUG;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("helm");
        HELM_BASE_THRUST = builder
                .comment("Base helm thrust force before mass and physics timestep scaling.")
                .defineInRange("baseThrust", 10.0, 0.0, 1000.0);
        HELM_TURN_FORCE = builder
                .comment("Base helm turn force before mass and physics timestep scaling.")
                .defineInRange("turnForce", 30.0, 0.0, 1000.0);
        HELM_WATER_SPEED_CAP = builder
                .comment("Base forward speed cap while the ship footprint touches water.")
                .defineInRange("waterSpeedCap", 5.0, 0.0, 1000.0);
        HELM_LAND_SPEED_CAP = builder
                .comment("Base forward speed cap when the ship is not detected on water.")
                .defineInRange("landSpeedCap", 2.5, 0.0, 1000.0);
        builder.pop();

        builder.push("ship_engine");
        SHIP_ENGINE_THRUST_BONUS = builder
                .comment("Extra helm thrust force added by each powered Ship Engine.")
                .defineInRange("thrustBonus", 5.0, 0.0, 1000.0);
        SHIP_ENGINE_WATER_SPEED_CAP_BONUS = builder
                .comment("Extra water speed cap added by each powered Ship Engine.")
                .defineInRange("waterSpeedCapBonus", 2.0, 0.0, 1000.0);
        SHIP_ENGINE_MAX_STACKING_ENGINES = builder
                .comment("Maximum powered Ship Engines that can boost one helm.")
                .defineInRange("maxStackingEngines", 16, 1, 64);
        SHIP_ENGINE_SCAN_INTERVAL_TICKS = builder
                .comment("How often a helm scans its SubLevel for powered Ship Engines.")
                .defineInRange("engineScanIntervalTicks", 20, 1, 200);
        builder.pop();

        builder.push("sync");
        HUD_DISTANCE_BLOCKS = builder
                .comment("Maximum helm distance for the pilot HUD and active control state.")
                .defineInRange("hudDistanceBlocks", 10, 5, 100);
        TELEMETRY_RADIUS = builder
                .comment("Radius around a helm where server telemetry packets may be sent.")
                .defineInRange("telemetryRadius", 64.0, 5.0, 512.0);
        IDLE_SYNC_INTERVAL_TICKS = builder
                .comment("Minimum interval for non-pilot helm telemetry sync.")
                .defineInRange("idleSyncIntervalTicks", 20, 1, 200);
        PROXIMITY_CHECK_INTERVAL_TICKS = builder
                .comment("How often active pilot proximity is rechecked.")
                .defineInRange("proximityCheckIntervalTicks", 10, 1, 200);
        builder.pop();

        builder.push("stabilization");
        GYRO_STRENGTH = builder
                .comment(
                        "Simple stabilization strength. This is PID kp: how strongly the ship tries to stand upright.",
                        "Higher values recover from tilt faster, but too high can make the ship snap or shake."
                )
                .defineInRange("stabilizationStrength", 300.0, 0.0, 2000.0);
        GYRO_DAMPING = builder
                .comment(
                        "Simple stabilization damping. This is PID kd: how much wobble and rotation gets damped.",
                        "Higher values calm rocking, but too high can make steering feel heavy."
                )
                .defineInRange("stabilizationDamping", 12.0, 0.0, 200.0);
        GYRO_CORRECTION = builder
                .comment(
                        "Simple stabilization correction. This is PID ki: slow correction over time; keep it low.",
                        "Small values help remove long-term lean. Large values can build up and cause oscillation."
                )
                .defineInRange("stabilizationCorrection", 3.5, 0.0, 50.0);
        builder.pop();

        builder.push("debug");
        DEBUG = builder
                .comment("Debug. Please dont toggle if you dont know what are you doing. This spams so much console log")
                .define("debugState", false);
        builder.pop();

        SPEC = builder.build();
    }

    private SableShipsConfig() {}


}
