package net.enflky.sable_ships.menu;

import net.enflky.sable_ships.SableShipsBlocks;
import net.enflky.sable_ships.content.engine.ShipEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class ShipEngineMenu extends AbstractContainerMenu {

    public static final int ENGINE_SLOT = 0;

    public final ShipEngineBlockEntity blockEntity;
    public final BlockPos blockPos;
    private DataSlot burnTimeSlot;
    private DataSlot burnDurationSlot;

    public ShipEngineMenu(int containerId, Inventory playerInventory, ShipEngineBlockEntity blockEntity) {
        super(ModMenuTypes.SHIP_ENGINE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.blockPos = blockEntity.getBlockPos();

        addSlot(new FuelSlot(blockEntity, 80, 35));
        addPlayerInventory(playerInventory);
        bindEngineData(blockEntity);
    }

    public ShipEngineMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.SHIP_ENGINE_MENU.get(), containerId);
        this.blockPos = extraData.readBlockPos();
        this.blockEntity = null;

        addSlot(new Slot(new net.minecraft.world.SimpleContainer(1), ENGINE_SLOT, 80, 35));
        addPlayerInventory(playerInventory);
        burnTimeSlot = addDataSlot(DataSlot.standalone());
        burnDurationSlot = addDataSlot(DataSlot.standalone());
    }

    public boolean isBurning() {
        return getBurnTime() > 0;
    }

    public int getBurnTime() {
        return burnTimeSlot.get();
    }

    public int getBurnDuration() {
        return burnDurationSlot.get();
    }

    public int getBurnProgressPixels() {
        int duration = getBurnDuration();
        if (duration <= 0) {
            return 0;
        }
        return getBurnTime() * 13 / duration;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) {
            return true;
        }
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(blockEntity.getLevel(), blockPos),
                player, SableShipsBlocks.SHIP_ENGINE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return result;
        }

        ItemStack source = slot.getItem();
        result = source.copy();

        if (index == ENGINE_SLOT) {
            if (!moveItemStackTo(source, 1, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (isFuel(source)) {
            if (!moveItemStackTo(source, ENGINE_SLOT, ENGINE_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 28) {
            if (!moveItemStackTo(source, 28, 37, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 37 && !moveItemStackTo(source, 1, 28, false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private void bindEngineData(ShipEngineBlockEntity engine) {
        burnTimeSlot = addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return engine.data.get(0);
            }

            @Override
            public void set(int value) {
                engine.data.set(0, value);
            }
        });
        burnDurationSlot = addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return engine.data.get(1);
            }

            @Override
            public void set(int value) {
                engine.data.set(1, value);
            }
        });
    }

    private static boolean isFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.getFuel().containsKey(stack.getItem());
    }

    private static class FuelSlot extends Slot {
        FuelSlot(ShipEngineBlockEntity container, int x, int y) {
            super(container, ENGINE_SLOT, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isFuel(stack);
        }
    }
}
