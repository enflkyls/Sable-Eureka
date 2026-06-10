package net.enflky.sable_ships;

import net.enflky.sable_ships.content.ShipHelmBlock;
import net.enflky.sable_ships.content.ShipHelmBlockEntity;
import net.enflky.sable_ships.content.engine.ShipEngineBlockEntity;
import net.enflky.sable_ships.content.seat.SeatBlock;
import net.enflky.sable_ships.content.seat.SeatBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SableShipsBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SableShips.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShipHelmBlockEntity>> SHIP_HELM =
            BLOCK_ENTITIES.register("ship_helm", () -> {
                ShipHelmBlock[] blocks = SableShipsBlocks.SHIP_HELMS.values()
                        .stream()
                        .map(DeferredHolder::value)
                        .toArray(ShipHelmBlock[]::new);
                return BlockEntityType.Builder.of(ShipHelmBlockEntity::new, blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeatBlockEntity>> SEAT =
            BLOCK_ENTITIES.register("seat_block_entity", () -> {
                SeatBlock[] blocks = SableShipsBlocks.SEATS.values()
                        .stream()
                        .map(DeferredHolder::value)
                        .toArray(SeatBlock[]::new);
                return BlockEntityType.Builder.of(SeatBlockEntity::new, blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShipEngineBlockEntity>> SHIP_ENGINE =
            BLOCK_ENTITIES.register("ship_engine", () ->
                    BlockEntityType.Builder.of(ShipEngineBlockEntity::new, SableShipsBlocks.SHIP_ENGINE.get()).build(null));

    private SableShipsBlockEntityTypes() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
