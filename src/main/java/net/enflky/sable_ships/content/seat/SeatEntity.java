package net.enflky.sable_ships.content.seat;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SeatEntity extends Entity {

    private BlockPos seatBlockPos = BlockPos.ZERO;

    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setSeatBlockPos(BlockPos pos) {
        this.seatBlockPos = pos;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        seatBlockPos = new BlockPos(tag.getInt("bx"), tag.getInt("by"), tag.getInt("bz"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("bx", seatBlockPos.getX());
        tag.putInt("by", seatBlockPos.getY());
        tag.putInt("bz", seatBlockPos.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (level().isClientSide) return;
        if (getPassengers().isEmpty()) {
            discard();
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty() && passenger instanceof Player;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        return new Vec3(getX(), getY() + 1, getZ());
    }

    @Override
    public boolean isInvisible() { return true; }

    @Override
    public boolean isPickable() { return false; }


}