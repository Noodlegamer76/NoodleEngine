package com.noodlegamer76.noodleengine.client.glitf.skin;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshNodeHierarchy;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;

import java.util.*;

public class NodeLoader {


    public static void loadNodeHierarchy(McGltf model) {
        for(int i = 0; i < model.model.getNodeModels().size(); i++) {
            NodeModel nodeModel = model.model.getNodeModels().get(i);
            model.nodes.add(nodeModel);
            if (!nodeModel.getMeshModels().isEmpty()) {
                MeshData meshData = model.meshModelToMeshData.get(nodeModel.getMeshModels().get(0));
                model.meshToNode.put(meshData, nodeModel);
            }
        }

        for (NodeModel node : model.nodes) {

            for (MeshModel meshModel : node.getMeshModels()) {
                MeshData meshData = model.meshModelToMeshData.get(meshModel);

                List<Integer> parents = new ArrayList<>();
                NodeModel current = node;
                while (current.getParent() != null) {
                    NodeModel parent = current.getParent();
                    parents.add(model.nodes.indexOf(parent));
                    current = parent;
                }
                Collections.reverse(parents);
                MeshNodeHierarchy meshNodeHierarchy = new MeshNodeHierarchy(node, parents);
                model.meshNodeHierarchy.put(meshData, meshNodeHierarchy);
                meshData.setHierarchy(meshNodeHierarchy);
            }
        }
    }
}
