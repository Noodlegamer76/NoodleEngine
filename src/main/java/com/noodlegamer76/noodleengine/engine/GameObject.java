package com.noodlegamer76.noodleengine.engine;

import com.noodlegamer76.noodleengine.engine.components.Component;
import com.noodlegamer76.noodleengine.engine.network.GameObjectSerializers;
import com.noodlegamer76.noodleengine.engine.network.SyncedVar;
import com.noodlegamer76.noodleengine.engine.network.SyncedVarOwner;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class GameObject implements SyncedVarOwner {
    private final Map<Integer, SyncedVar<?>> syncedVariables = new HashMap<>();
    private final List<SyncedVar<?>> dirtySyncedVariables = new ArrayList<>();
    private final SyncedVar<Vector3f> position = new SyncedVar<>(this, new Vector3f(), GameObjectSerializers.VECTOR3F);
    private final SyncedVar<Quaternionf> rotation = new SyncedVar<>(this, new Quaternionf(), GameObjectSerializers.QUATERNIONF);
    private final SyncedVar<Vector3f> scale = new SyncedVar<>(this, new Vector3f(1, 1, 1), GameObjectSerializers.VECTOR3F);
    private final Map<Class<? extends Component>, List<Component>> components = new HashMap<>();
    private int nextSyncedVarId = 0;
    private final UUID id;
    private Level level;

    public GameObject(Level level) {
        this(UUID.randomUUID(), level);
    }

    public GameObject(UUID uuid) {
        this(uuid, null);
    }

    public GameObject(UUID uuid, Level level) {
        id = uuid;
        this.level = level;

        addSyncedVar(position);
        addSyncedVar(rotation);
        addSyncedVar(scale);
    }

    public void addSyncedVar(SyncedVar<?> var) {
        var.setId(nextSyncedVarId());
        syncedVariables.put(var.getId(), var);
        dirtySyncedVariables.add(var);
    }

    public void addComponent(Component component) {
        if (!components.containsKey(component.getClass())) {
            components.put(component.getClass(), new ArrayList<>());
        }

        if (components.get(component.getClass()).contains(component)) {
            return;
        }

        components.get(component.getClass()).add(component);
        component.setGameObject(this);

        for (SyncedVar<?> var: component.getSyncedData()) {
            var.setId(nextSyncedVarId++);
            syncedVariables.put(var.getId(), var);
            dirtySyncedVariables.add(var);
        }

    }

    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getComponents(Class<T> clazz) {
        if (!components.containsKey(clazz)) {
            return List.of();
        }

        return (List<T>) components.get(clazz);
    }

    public void removeComponents(Class<? extends Component> clazz) {
        if (!components.containsKey(clazz)) {
            return;
        }

        for (Component component : components.get(clazz)) {
            for (SyncedVar<?> var: component.getSyncedData()) {
                syncedVariables.remove(var.getId());
                dirtySyncedVariables.remove(var);
            }
        }

        components.remove(clazz);
    }

    public void removeComponent(Component component) {
        if (!components.containsKey(component.getClass())) {
            return;
        }

        for (SyncedVar<?> var: component.getSyncedData()) {
            syncedVariables.remove(var.getId());
            dirtySyncedVariables.remove(var);
        }

        components.get(component.getClass()).remove(component);
    }

    private int nextSyncedVarId() {
        return nextSyncedVarId++;
    }

    public Map<Class<? extends Component>, List<Component>> getComponents() {
        return components;
    }

    public Map<Integer, SyncedVar<?>> getSyncedVariables() {
        return syncedVariables;
    }

    public List<SyncedVar<?>> getDirtySyncedVariables() {
        return dirtySyncedVariables;
    }

    public void setPosition(Vector3f position) {
        this.position.setValue(position);
        dirtySyncedVariables.add(this.position);
    }

    public SyncedVar<Vector3f> getPosition() {
        return position;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.setValue(rotation);
        dirtySyncedVariables.add(this.rotation);
    }

    public SyncedVar<Quaternionf> getRotation() {
        return rotation;
    }

    public void setScale(Vector3f scale) {
        this.scale.setValue(scale);
        dirtySyncedVariables.add(this.scale);
    }

    public SyncedVar<Vector3f> getScale() {
        return scale;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public void markDirty(SyncedVar<?> var) {
        if (!dirtySyncedVariables.contains(var)) dirtySyncedVariables.add(var);
    }

    @Override
    public List<SyncedVar<?>> getSyncedData() {
        return List.of(
                position,
                rotation,
                scale
        );
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
