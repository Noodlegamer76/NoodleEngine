package com.noodlegamer76.noodleengine.client.glitf.mesh;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.TextureModel;
import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record Vertex(
        float x, float y, float z,
        float nx, float ny, float nz,
        Map<Integer, Vector2f> UVs,
        float[] jointIndices, float[] jointWeights
) {

}
