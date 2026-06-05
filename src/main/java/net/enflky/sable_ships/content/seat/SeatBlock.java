package net.enflky.sable_ships.content.seat;

import com.mojang.serialization.MapCodec;
import net.enflky.sable_ships.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class SeatBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 9, 14);

    public SeatBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SeatBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SeatBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        List<SeatEntity> existing = level.getEntitiesOfClass(
                SeatEntity.class, new AABB(pos).inflate(0.1)
        );
        if (!existing.isEmpty() || player.getVehicle() != null)
            return InteractionResult.PASS;

        // summon at plot for truth hohoh dad joke style comment but fuck it who reads these?? me or some guys scared of malware or some guy madding addon (0% for this shit code) or some guy stealing code also %0 chance beacuse this code base is SHIT
        SeatEntity seat = new SeatEntity(ModEntityTypes.SEAT.get(), level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + 0.45, pos.getZ() + 0.5);
        level.addFreshEntity(seat);

        if (level.getBlockEntity(pos) instanceof SeatBlockEntity sbe) {
            sbe.setSeatEntity(seat);
        }

        player.startRiding(seat);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide) {
            level.getEntitiesOfClass(SeatEntity.class, new AABB(pos).inflate(0.1))
                    .forEach(e -> {
                        e.ejectPassengers();
                        e.discard();
                    });
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}