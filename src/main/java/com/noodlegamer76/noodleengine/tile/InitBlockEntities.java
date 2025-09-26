package com.noodlegamer76.noodleengine.tile;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.block.InitBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class InitBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NoodleEngine.MODID);

    public static final RegistryObject<BlockEntityType<RenderTestTile>> RENDER_TEST = BLOCK_ENTITIES.register("render_test",
            () -> BlockEntityType.Builder.of(RenderTestTile::new, InitBlocks.RENDER_TEST.get()).build(null));
}
