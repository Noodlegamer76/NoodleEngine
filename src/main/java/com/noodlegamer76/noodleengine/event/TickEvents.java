package com.noodlegamer76.noodleengine.event;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.engine.GameObject;
import com.noodlegamer76.noodleengine.engine.GameObjects;
import com.noodlegamer76.noodleengine.engine.components.Component;
import com.noodlegamer76.noodleengine.engine.components.TickableComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = NoodleEngine.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TickEvents {

    @SubscribeEvent
    public static void onTick(TickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        for (GameObject object: GameObjects.getGameObjects().values()) {
            for (List<Component> components: object.getComponents().values()) {
                for (Component component: components) {
                    if (component instanceof TickableComponent tickableComponent) {
                        tickableComponent.tick();
                    }
                }
            }
        }
    }
}
