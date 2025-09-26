package com.noodlegamer76.noodleengine.client.glitf.skin;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Skin;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL31;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinLoader {

    public static void loadSkins(McGltf model) {
        if (model.gltf.getSkins() == null) return;

        List<Matrix4f> bindGlobals = BindPoseUtils.buildBindPoseGlobals(model);
        model.bindGlobalPose.clear();
        model.bindGlobalPose.addAll(bindGlobals);

        List<SkinUbo> skinUbos = new ArrayList<>();
        for (Skin skin : model.gltf.getSkins()) {
            int nodeCount = model.gltf.getNodes().size();
            SkinUbo ubo = new SkinUbo(model, skin, nodeCount);
            skinUbos.add(ubo);
            model.skins.add(ubo);
        }

        Map<MeshData, List<SkinUbo>> skins = new HashMap<>();
        for (MeshData mesh : model.meshes) {
            skins.put(mesh, new ArrayList<>());
        }

        for (Node node : model.gltf.getNodes()) {
            if (node.getMesh() != null && node.getSkin() != null) {
                int meshIndex = node.getMesh();
                int skinIndex = node.getSkin();
                MeshData mesh = model.meshes.get(meshIndex);
                SkinUbo skin = skinUbos.get(skinIndex);

                skins.get(mesh).add(skin);
                mesh.availableSkins.add(skin);
            }
        }

        model.skinsFromMesh.putAll(skins);

        for (Node node : model.gltf.getNodes()) {
            if (node.getMesh() != null && node.getSkin() != null) {
                int meshIndex = node.getMesh();
                int skinIndex = node.getSkin();

                MeshData mesh = model.meshes.get(meshIndex);
                SkinUbo skin = skinUbos.get(skinIndex);

                int nodeIndex = model.gltf.getNodes().indexOf(node);
                skin.upload(bindGlobals, nodeIndex);
            }
        }
    }
}

