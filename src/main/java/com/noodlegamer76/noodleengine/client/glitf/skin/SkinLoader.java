package com.noodlegamer76.noodleengine.client.glitf.skin;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL31;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinLoader {

    public static void loadSkins(McGltf model) {
        if (model.model.getSkinModels() == null) return;

        List<Matrix4f> bindGlobals = BindPoseUtils.buildBindPoseGlobals(model);
        model.bindGlobalPose.addAll(bindGlobals);


        List<SkinUbo> skinUbos = new ArrayList<>();

        for (SkinModel skin : model.model.getSkinModels()) {
            int nodeCount = model.nodes.size();
            SkinUbo ubo = new SkinUbo(model, skin, nodeCount);
            skinUbos.add(ubo);
            model.skins.add(ubo);
        }

        Map<MeshData, List<SkinUbo>> skins = new HashMap<>();
        for (MeshData mesh : model.meshes) {
            skins.put(mesh, new ArrayList<>());
        }

        for (NodeModel node : model.nodes) {
            if (!node.getMeshModels().isEmpty() && node.getSkinModel() != null) {
                MeshData mesh = model.meshModelToMeshData.get(node.getMeshModels().get(0));
                SkinUbo skin = skinUbos.get(model.model.getSkinModels().indexOf(node.getSkinModel()));

                skins.get(mesh).add(skin);
                mesh.availableSkins.add(skin);
            }
        }

        for (MeshData mesh : model.meshes) {
            if (mesh.availableSkins.isEmpty()) {
                int nodeCount = model.nodes.size();
                SkinUbo ubo = new SkinUbo(model, null, nodeCount);
                mesh.availableSkins.add(ubo);
                model.skins.add(ubo);
            }
        }

        model.skinsFromMesh.putAll(skins);

        for (NodeModel node : model.nodes) {
            if  (!node.getMeshModels().isEmpty() && node.getSkinModel() != null) {
                SkinUbo skin = skinUbos.get(model.model.getSkinModels().indexOf(node.getSkinModel()));

                int nodeIndex = model.nodes.indexOf(node);
                skin.uploadRigged(bindGlobals, nodeIndex);
            }
        }
    }
}

