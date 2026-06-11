package net.enflky.sable_ships;

import net.enflky.sable_ships.content.BallastBlock;
import net.enflky.sable_ships.content.ShipHelmBlock;
import net.enflky.sable_ships.content.engine.ShipEngineBlock;
import net.enflky.sable_ships.content.floater.FloaterBlock;
import net.enflky.sable_ships.content.seat.SeatBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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

    public static final Map<String, DeferredHolder<Block, ShipHelmBlock>> SHIP_HELMS = new HashMap<>();
    public static final Map<String, DeferredHolder<Block, SeatBlock>> SEATS = new HashMap<>();

    public static final DeferredHolder<Block, ShipEngineBlock> SHIP_ENGINE;
    public static final DeferredHolder<Block, FloaterBlock> FLOATER;
    public static final DeferredHolder<Block, BallastBlock> BALLAST;

    static {
        String[] woodTypes = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};

        for (String wood : woodTypes) {
            DeferredHolder<Block, ShipHelmBlock> helmBlock = register(
                    wood + "_ship_helm",
                    () -> new ShipHelmBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                            .strength(2.5F)
                            .noOcclusion()
                    )
            );
            SHIP_HELMS.put(wood, helmBlock);
            DeferredHolder<Block, SeatBlock> seatBlock = register(
                    wood + "_seat",
                    () -> new SeatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                            .strength(2.5F)
                            .noOcclusion()
                    )
            );
            SEATS.put(wood, seatBlock);
        }

        SHIP_ENGINE = register("ship_engine",
                () -> new ShipEngineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
                        .strength(3.5F)
                        .noOcclusion()
                )
        );

        FLOATER = register("floater",
                () -> new FloaterBlock(BlockBehaviour.Properties.of()
                        .sound(SoundType.WOOL)
                        .strength(0.5F)
                        .noOcclusion()
                )
        );

        BALLAST = register("ballast",
                () -> new BallastBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)
                        .strength(3.5F)
                        .noOcclusion()
                )
        );
    }

    private SableShipsBlocks() {}

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
