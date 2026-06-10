package net.enflky.sable_ships.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SableShipsConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue SHIP_ENGINE_THRUST_BONUS;
    public static final ModConfigSpec.DoubleValue SHIP_ENGINE_WATER_SPEED_CAP_BONUS;
    public static final ModConfigSpec.IntValue SHIP_ENGINE_MAX_STACKING_ENGINES;
    public static final ModConfigSpec.IntValue HUD_DISTANCE;
    public static final ModConfigSpec.DoubleValue GYRO_STRENGTH;
    public static final ModConfigSpec.DoubleValue GYRO_DAMPING;
    public static final ModConfigSpec.DoubleValue GYRO_CORRECTION;
    public static final ModConfigSpec.BooleanValue DEBUG;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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
        HUD_DISTANCE = builder
                .comment("Hud distance if you have problems with ship helm control dropping for no reason look for it.")
                .defineInRange("maxHudDistance", 10, 5, 100);
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
