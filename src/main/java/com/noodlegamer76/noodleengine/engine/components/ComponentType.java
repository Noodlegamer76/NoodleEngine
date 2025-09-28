package com.noodlegamer76.noodleengine.engine.components;

import java.util.function.Supplier;

public class ComponentType {
    private final Supplier<Component> factory;

    public ComponentType(Supplier<Component> factory) {
        this.factory = factory;
    }

    public Component create() {
        return factory.get();
    }
}
