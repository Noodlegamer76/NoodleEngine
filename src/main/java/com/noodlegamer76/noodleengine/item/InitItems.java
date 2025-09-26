package com.noodlegamer76.noodleengine.item;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.block.InitBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, NoodleEngine.MODID);

    public static final RegistryObject<TestItem> TEST_ITEM = ITEMS.register("test_item",
            () -> new TestItem(new Item.Properties()));

    public static final RegistryObject<BlockItem> RENDER_TEST = ITEMS.register("render_test",
            () -> new BlockItem(InitBlocks.RENDER_TEST.get(), new Item.Properties()));
}
