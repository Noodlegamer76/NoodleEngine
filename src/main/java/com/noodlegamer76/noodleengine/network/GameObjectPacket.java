package com.noodlegamer76.noodleengine.network;

import com.noodlegamer76.noodleengine.engine.GameObject;
import com.noodlegamer76.noodleengine.engine.GameObjects;
import com.noodlegamer76.noodleengine.engine.components.Component;
import com.noodlegamer76.noodleengine.engine.components.ComponentType;
import com.noodlegamer76.noodleengine.engine.components.InitComponents;
import com.noodlegamer76.noodleengine.engine.network.SyncedVar;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GameObjectPacket {
    public GameObject object;

    public GameObjectPacket(GameObject object) {
        this.object = object;
    }

    public GameObjectPacket(FriendlyByteBuf buf) {
        object = new GameObject(buf.readUUID(), Minecraft.getInstance().level);

        int baseObjectDataSize = buf.readVarInt();
        for (int i = 0; i < baseObjectDataSize; i++) {
            SyncedVar<?> var = object.getSyncedData().get(i);
            setVarValue(var, buf);
        }

        int components = buf.readVarInt();
        for (int i = 0; i < components; i++) {
            ResourceLocation location = buf.readResourceLocation();
            ComponentType type = InitComponents.getComponentTypes().get(location);

            Component component = type.create();

            int componentDataSize = buf.readVarInt();
            for (int j = 0; j < componentDataSize; j++) {
                SyncedVar<?> var = component.getSyncedData().get(j);
                setVarValue(var, buf);
            }

            object.addComponent(component);
        }

        GameObjects.addGameObject(object);
    }

    public <T> void setVarValue(SyncedVar<T> var, FriendlyByteBuf buf) {
        T object = var.getSerializer().deserialize(buf);
        var.setValue(object);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(object.getId());

        List<SyncedVar<?>> gameObjectData = object.getSyncedData();
        buf.writeVarInt(gameObjectData.size());
        for (SyncedVar<?> var : gameObjectData) {
            @SuppressWarnings("unchecked")
            SyncedVar<Object> v = (SyncedVar<Object>) var;
            v.getSerializer().serialize(buf, v.getValue());
        }

        buf.writeVarInt(object.getComponents().size());
        for (Map.Entry<Class<? extends Component>, List<Component>> entry: object.getComponents().entrySet()) {
            for (Component component : entry.getValue()) {
                ResourceLocation location = component.getType().getId();
                buf.writeResourceLocation(location);

                buf.writeVarInt(component.getSyncedData().size());
                for (SyncedVar<?> var : component.getSyncedData()) {
                    @SuppressWarnings("unchecked")
                    SyncedVar<Object> v = (SyncedVar<Object>) var;
                    v.getSerializer().serialize(buf, v.getValue());
                }
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        GameObjects.addGameObject(object);

        context.setPacketHandled(true);
    }
}