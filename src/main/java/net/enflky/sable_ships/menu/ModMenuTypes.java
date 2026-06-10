package net.enflky.sable_ships.menu;

import net.enflky.sable_ships.SableShips;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, SableShips.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ShipHelmMenu>> SHIP_HELM_MENU =
            MENU_TYPES.register("ship_helm_menu",
                    () -> IMenuTypeExtension.create(ShipHelmMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ShipEngineMenu>> SHIP_ENGINE_MENU =
            MENU_TYPES.register("ship_engine_menu",
                    () -> IMenuTypeExtension.create(ShipEngineMenu::new));

    private ModMenuTypes() {}

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
