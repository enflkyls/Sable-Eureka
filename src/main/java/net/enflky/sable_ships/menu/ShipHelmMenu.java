package net.enflky.sable_ships.menu;

import net.enflky.sable_ships.content.ShipHelmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ShipHelmMenu extends AbstractContainerMenu {

    public final ShipHelmBlockEntity blockEntity;
    public final BlockPos blockPos;
    public final boolean autoPilot;

    public ShipHelmMenu(int containerId, Inventory playerInventory,
                        ShipHelmBlockEntity blockEntity, boolean autoPilot) {
        super(ModMenuTypes.SHIP_HELM_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.blockPos = blockEntity.getBlockPos();
        this.autoPilot = autoPilot;
    }

    public ShipHelmMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.SHIP_HELM_MENU.get(), containerId);
        this.blockPos = extraData.readBlockPos();
        this.autoPilot = extraData.readBoolean();
        this.blockEntity = null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) {
            return true;
        }
        return player.distanceToSqr(
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5) < 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
