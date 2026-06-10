package net.enflky.sable_ships;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SableShipsItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, SableShips.MOD_ID);

    static {
        SableShipsBlocks.SHIP_HELMS.forEach((wood, block) ->
                ITEMS.register(wood + "_ship_helm", () -> new BlockItem(block.get(), new Item.Properties())));
        SableShipsBlocks.SEATS.forEach((wood, block) ->
                ITEMS.register(wood + "_seat", () -> new BlockItem(block.get(), new Item.Properties())));
        ITEMS.register("ship_engine", () -> new BlockItem(SableShipsBlocks.SHIP_ENGINE.get(), new Item.Properties()));
        ITEMS.register("floater", () -> new BlockItem(SableShipsBlocks.FLOATER.get(), new Item.Properties()));
    }

    private SableShipsItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
