package com.noodlegamer76.noodleengine.engine.network;

import com.noodlegamer76.noodleengine.engine.components.Component;

import java.util.List;

public interface SyncedVarOwner {
    void markDirty(SyncedVar<?> var);

    List<SyncedVar<?>> getSyncedData();
}

