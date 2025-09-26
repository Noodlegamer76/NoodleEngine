package com.noodlegamer76.noodleengine.client.glitf.mesh;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import com.noodlegamer76.noodleengine.client.glitf.rendering.GltfVbo;
import de.javagl.jgltf.impl.v2.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VboCreator {
    public static void createVbos(McGltf model) {
        Map<McMaterial, List<PrimitiveData>> materials = new HashMap<>();

        for (int i = 0; i < model.meshes.size(); i++) {
            MeshData mesh = model.meshes.get(i);

            for (int j = 0; j < mesh.getPrimitives().size(); j++) {
                PrimitiveData primitive = mesh.getPrimitives().get(j);

                if (!materials.containsKey(primitive.getMaterial())) {
                    materials.put(primitive.getMaterial(), new ArrayList<>());
                }

                materials.get(primitive.getMaterial()).add(primitive);
            }
        }

        model.materialToPrimitives.putAll(materials);

        for (McMaterial material : materials.keySet()) {
            List<PrimitiveData> primitives = materials.get(material);

            GltfVbo vbo = new GltfVbo(VertexBuffer.Usage.STATIC, material);

            for (PrimitiveData primitive: primitives) {
                vbo.addPrimitive(primitive);
                primitive.setVbo(vbo);
                model.vboMap.put(material, vbo);
            }
        }
    }
}
