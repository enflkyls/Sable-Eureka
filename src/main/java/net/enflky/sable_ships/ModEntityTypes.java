package net.enflky.sable_ships;

import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.content.seat.SeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SableShips.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT =
            ENTITY_TYPES.register("seat", () ->
                    EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                            .sized(0.001f, 0.001f)
                            .clientTrackingRange(10)
                            .updateInterval(20)  // update maybe fix that lattter
                            .build("seat")
            );

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}