package net.enflky.sable_ships.content.seat;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShipsBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SeatBlockEntity extends BlockEntity implements BlockEntitySubLevelActor {

    private SeatEntity seatEntity = null;

    public SeatBlockEntity(BlockPos pos, BlockState state) {
        super(SableShipsBlockEntityTypes.SEAT.get(), pos, state);
    }

    public void setSeatEntity(SeatEntity entity) {
        this.seatEntity = entity;
    }

    @Override
    public void sable$physicsTick(final ServerSubLevel subLevel, final RigidBodyHandle handle, final double timeStep) {
        if (seatEntity == null || seatEntity.isRemoved()) return;

        seatEntity.setPos(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.45,
                worldPosition.getZ() + 0.5
        );
    }
}
