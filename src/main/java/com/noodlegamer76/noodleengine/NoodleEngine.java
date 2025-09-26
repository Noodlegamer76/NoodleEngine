package com.noodlegamer76.noodleengine;

import com.mojang.logging.LogUtils;
import com.noodlegamer76.noodleengine.block.InitBlocks;
import com.noodlegamer76.noodleengine.item.InitItems;
import com.noodlegamer76.noodleengine.tile.InitBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
