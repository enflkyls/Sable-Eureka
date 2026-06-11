package net.enflky.sable_ships.content.floater;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FloaterBlock extends Block {

    public FloaterBlock(Properties properties) {
        super(properties);
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


}
