package net.enflky.sable_ships;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SableShipsCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SableShips.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
            CREATIVE_MODE_TABS.register("main", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.sable_ships"))
                            .icon(() -> SableShipsBlocks.SHIP_HELMS.get("oak").get().asItem().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                SableShipsBlocks.SHIP_HELMS.values().forEach(block -> output.accept(block.get()));
                                SableShipsBlocks.SEATS.values().forEach(block -> output.accept(block.get()));
                                output.accept(SableShipsBlocks.SHIP_ENGINE.get());
                                output.accept(SableShipsBlocks.FLOATER.get());
                            })
                            .build());

    private SableShipsCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
