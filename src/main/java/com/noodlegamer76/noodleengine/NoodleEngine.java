package com.noodlegamer76.noodleengine;

import com.mojang.logging.LogUtils;
import com.noodlegamer76.noodleengine.block.InitBlocks;
import com.noodlegamer76.noodleengine.engine.components.InitComponents;
import com.noodlegamer76.noodleengine.item.InitItems;
import com.noodlegamer76.noodleengine.tile.InitBlockEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NoodleEngine.MODID)
public class NoodleEngine
{
    public static final String MODID = "noodleengine";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NoodleEngine(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        InitItems.ITEMS.register(modEventBus);
        InitBlocks.BLOCKS.register(modEventBus);
        InitBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        InitComponents.COMPONENT_TYPES.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
