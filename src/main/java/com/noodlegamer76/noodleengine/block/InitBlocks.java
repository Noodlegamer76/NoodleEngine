package com.noodlegamer76.noodleengine.block;

import com.noodlegamer76.noodleengine.NoodleEngine;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class InitBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, NoodleEngine.MODID);

    public static final RegistryObject<RenderTestBlock> RENDER_TEST = BLOCKS.register("render_test",
            () -> new RenderTestBlock(Block.Properties.of().noOcclusion()));
}
