package com.noodlegamer76.noodleengine.engine.network;

public class SyncedVar<T> {
    private T value;
    private final GameObjectSerializer<T> serializer;
    private int id;
    private final SyncedVarOwner owner;

    public SyncedVar(SyncedVarOwner owner, T value, GameObjectSerializer<T> serializer) {
        this.serializer = serializer;
        this.owner = owner;
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
        owner.markDirty(this);
    }

    public T getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public GameObjectSerializer<T> getSerializer() {
        return serializer;
    }
}
