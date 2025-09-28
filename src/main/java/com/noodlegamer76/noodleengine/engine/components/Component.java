package com.noodlegamer76.noodleengine.engine.components;

import com.noodlegamer76.noodleengine.engine.GameObject;
import com.noodlegamer76.noodleengine.engine.network.SyncedVar;
import com.noodlegamer76.noodleengine.engine.network.SyncedVarOwner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public abstract class Component implements SyncedVarOwner {
    public GameObject gameObject;
    private final RegistryObject<ComponentType> type;

    public Component(RegistryObject<ComponentType> type) {
        this.type = type;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public List<SyncedVar<?>> getSyncedData() {
        return List.of();
    }

    @Override
    public void markDirty(SyncedVar<?> var) {
        if (getGameObject() == null) {
            return;
        }
        getGameObject().markDirty(var);
    }

    public RegistryObject<ComponentType> getType() {
        return type;
    }
}
