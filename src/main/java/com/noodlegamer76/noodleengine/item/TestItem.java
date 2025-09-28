package com.noodlegamer76.noodleengine.item;

import com.mojang.math.Axis;
import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.engine.GameObject;
import com.noodlegamer76.noodleengine.engine.GameObjects;
import com.noodlegamer76.noodleengine.engine.components.MeshRenderer;
import com.noodlegamer76.noodleengine.network.GameObjectPacket;
import com.noodlegamer76.noodleengine.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide) {
            GameObject gameObject = new GameObject(level);
            gameObject.setPosition(player.position().toVector3f());
            //gameObject.setScale(new Vector3f(0.00075f, 0.00075f, 0.00075f));
            //gameObject.setRotation(Axis.XP.rotation(-90));
            MeshRenderer renderer = new MeshRenderer();
            gameObject.addComponent(renderer);
            renderer.setModelLocation(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "gltf/miku_dance.glb"));
            GameObjects.addGameObject(gameObject);

            GameObjectPacket packet = new GameObjectPacket(gameObject);
            PacketHandler.sendToPlayer((ServerPlayer) player, packet);
        }

        return super.use(level, player, hand);
    }
}
