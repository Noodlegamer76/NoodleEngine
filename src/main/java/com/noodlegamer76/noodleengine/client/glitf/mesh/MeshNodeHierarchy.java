package com.noodlegamer76.noodleengine.client.glitf.mesh;

import de.javagl.jgltf.model.NodeModel;

import java.util.List;

public class MeshNodeHierarchy {
    public final NodeModel meshNode;
    public final List<Integer> parentIndices;

    public MeshNodeHierarchy(NodeModel meshNode, List<Integer> parentIndices) {
        this.meshNode = meshNode;
        this.parentIndices = parentIndices;
    }
}
