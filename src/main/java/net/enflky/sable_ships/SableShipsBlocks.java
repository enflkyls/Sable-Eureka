package net.enflky.sable_ships;

import net.enflky.sable_ships.content.ShipHelmBlock;
import net.enflky.sable_ships.content.ShipHelmBlockEntity;
import net.enflky.sable_ships.content.engine.ShipEngineBlock;
import net.enflky.sable_ships.content.engine.ShipEngineBlockEntity;
import net.enflky.sable_ships.content.seat.SeatBlock;
import net.enflky.sable_ships.content.seat.SeatBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SableShipsBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, SableShips.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, SableShips.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SableShips.MOD_ID);

    public static final Map<String, DeferredHolder<Block, ShipHelmBlock>> SHIP_HELMS = new HashMap<>();
    public static final Map<String, DeferredHolder<Block, SeatBlock>> SEATS = new HashMap<>();

    public static final DeferredHolder<Block, ShipEngineBlock> SHIP_ENGINE;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShipHelmBlockEntity>> SHIP_HELM_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeatBlockEntity>> SEAT_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShipEngineBlockEntity>> SHIP_ENGINE_ENTITY;

    static {
        String[] woodTypes = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};

        for (String wood : woodTypes) {
            DeferredHolder<Block, ShipHelmBlock> helmBlock = registerBlock(
                    wood + "_ship_helm",
                    () -> new ShipHelmBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                            .strength(2.5F))
            );
            SHIP_HELMS.put(wood, helmBlock);
            DeferredHolder<Block, SeatBlock> seatBlock = registerBlock(
                    wood + "_seat",
                    () -> new SeatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                            .strength(2.5F)
                            .noOcclusion())
            );
            SEATS.put(wood, seatBlock);
        }

        SHIP_ENGINE = registerBlock("ship_engine",
                () -> new ShipEngineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
                        .strength(3.5F)));

        SHIP_HELM_ENTITY = BLOCK_ENTITIES.register("ship_helm", () -> {
            ShipHelmBlock[] allBlocks = SHIP_HELMS.values()
                    .stream()
                    .map(DeferredHolder::value)
                    .toArray(ShipHelmBlock[]::new);
            return BlockEntityType.Builder.of(ShipHelmBlockEntity::new, allBlocks).build(null);
        });

        SEAT_BLOCK_ENTITY = BLOCK_ENTITIES.register("seat_block_entity", () -> {
            SeatBlock[] allSeats = SEATS.values()
                    .stream()
                    .map(DeferredHolder::value)
                    .toArray(SeatBlock[]::new);
            return BlockEntityType.Builder.of(SeatBlockEntity::new, allSeats).build(null);
        });

        SHIP_ENGINE_ENTITY = BLOCK_ENTITIES.register("ship_engine", () ->
                BlockEntityType.Builder.of(ShipEngineBlockEntity::new, SHIP_ENGINE.get()).build(null));
    }

    private SableShipsBlocks() {}

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> supplier) {
        DeferredHolder<Block, T> block = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }
}
