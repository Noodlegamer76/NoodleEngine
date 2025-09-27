package com.noodlegamer76.noodleengine.client.glitf.skin;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import de.javagl.jgltf.model.NodeModel;

public class NodeLoader {
    public static void loadNodes(McGltf model) {
        for(int i = 0; i < model.model.getNodeModels().size(); i++) {
            NodeModel nodeModel = model.model.getNodeModels().get(i);
            model.nodes.add(nodeModel);
            if (!nodeModel.getMeshModels().isEmpty()) {
                MeshData meshData = model.meshModelToMeshData.get(nodeModel.getMeshModels().get(0));
                model.meshToNode.put(meshData, nodeModel);
            }
        }
    }
}
