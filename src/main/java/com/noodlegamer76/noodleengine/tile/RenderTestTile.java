package com.noodlegamer76.noodleengine.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RenderTestTile extends BlockEntity {

    public RenderTestTile(BlockPos p_155229_, BlockState p_155230_) {
        super(InitBlockEntities.RENDER_TEST.get(), p_155229_, p_155230_);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(99999);
    }
}
