package net.enflky.sable_ships.content.engine;

import net.enflky.sable_ships.SableShipsBlockEntityTypes;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ShipEngineBlockEntity extends BlockEntity implements WorldlyContainer {

    private static final int SLOT_FUEL = 0;
    private static final int[] SLOTS = {SLOT_FUEL};

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private int burnTime;
    private int burnDuration;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnDuration;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnDuration = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ShipEngineBlockEntity(BlockPos pos, BlockState state) {
        super(SableShipsBlockEntityTypes.SHIP_ENGINE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ShipEngineBlockEntity engine) {
        boolean wasBurning = engine.isBurning();

        if (engine.burnTime > 0) {
            engine.burnTime--;
        }

        if (!engine.isBurning()) {
            ItemStack fuel = engine.items.get(SLOT_FUEL);
            int fuelTime = engine.getBurnDuration(fuel);
            if (fuelTime > 0) {
                ItemStack remainingItem = fuel.getCraftingRemainingItem();
                engine.burnTime = fuelTime;
                engine.burnDuration = fuelTime;
                fuel.shrink(1);
                if (fuel.isEmpty() && !remainingItem.isEmpty()) {
                    engine.items.set(SLOT_FUEL, remainingItem);
                }
            }
        }

        boolean isBurning = engine.isBurning();
        if (wasBurning != isBurning) {
            level.setBlock(pos, state.setValue(ShipEngineBlock.LIT, isBurning), 3);
        }

        if (wasBurning != isBurning) {
            engine.setChanged();
        }
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getBurnDuration() {
        return burnDuration;
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        int baseDuration = stack.getBurnTime(RecipeType.SMELTING);
        if (baseDuration <= 0 || !SableShipsConfig.SHIP_ENGINE_FUEL_MULTIPLIER_ENABLED.get()) {
            return baseDuration;
        }

        double multiplier = SableShipsConfig.SHIP_ENGINE_FUEL_DURATION_MULTIPLIER.get();
        return Math.max(1, (int) Math.ceil(baseDuration * multiplier));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        burnTime = tag.getInt("BurnTime");
        burnDuration = tag.getInt("BurnDuration");
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.get(SLOT_FUEL).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return getBurnDuration(stack) > 0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction direction) {
        return true;
    }
}
