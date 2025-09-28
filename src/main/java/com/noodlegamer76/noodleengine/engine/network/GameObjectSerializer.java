package com.noodlegamer76.noodleengine.engine.network;

import net.minecraft.network.FriendlyByteBuf;

public interface GameObjectSerializer<T> {
    void serialize(FriendlyByteBuf buffer, T value);

    T deserialize(FriendlyByteBuf buffer);

    static <T> GameObjectSerializer<T> create(FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return new GameObjectSerializer<>() {
            @Override
            public void serialize(FriendlyByteBuf buffer, T value) {
                writer.accept(buffer, value);
            }

            @Override
            public T deserialize(FriendlyByteBuf buffer) {
                return reader.apply(buffer);
            }
        };
    }


}
