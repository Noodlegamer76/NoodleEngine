package com.noodlegamer76.noodleengine.client.glitf.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.access.MeshStorage;
import com.noodlegamer76.noodleengine.client.glitf.access.ModelStorage;
import com.noodlegamer76.noodleengine.client.glitf.animation.AnimationLoader;
import com.noodlegamer76.noodleengine.client.glitf.material.GltfMaterialUtils;
import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.VboCreator;
import com.noodlegamer76.noodleengine.client.glitf.rendering.GltfRenderer;
import com.noodlegamer76.noodleengine.client.glitf.skin.NodeLoader;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinLoader;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GltfLoader {


    public static void loadAllGlbModels(ResourceManager resourceManager, String folder) {
        Map<ResourceLocation, Resource> locations = resourceManager.listResources(folder, path -> path.toString().endsWith(".glb"));
        for (Map.Entry<ResourceLocation, Resource> resource : locations.entrySet()) {
            McGltf model = loadModel(resource, resourceManager);
            if (model != null) {
                ModelStorage.addModel(resource.getKey(), model);
            }
        }
    }

    @Nullable
    public static McGltf loadModel(Map.Entry<ResourceLocation, Resource> resource, ResourceManager resourceManager) {
        try {
            InputStream resourceStream = resource.getValue().open();

            GltfModel gltfModel = new GltfModelReader().readWithoutReferences(resourceStream);

           McGltf model = new McGltf(gltfModel, resource.getKey());
           initModel(model);

           return model;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void initModel(McGltf model) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> initModel(model));
            return;
        }


        //load meshes and primitives into MeshData and PrimitiveData respectively
        storeMeshAndPrimitives(model);

        NodeLoader.loadNodeHierarchy(model);

        //load materials into McMaterial
        Map<Integer, McMaterial> materials = GltfMaterialUtils.loadMaterials(model);
        GltfMaterialUtils.assignMaterials(model, materials);

        SkinLoader.loadSkins(model);

        AnimationLoader.loadAnimations(model);

        //creates VertexBuffers for each material and renders primitives into them to be drawn later
        VboCreator.createVbos(model);
        GltfRenderer.renderMaterials(model);
    }

    private static void storeMeshAndPrimitives(McGltf model) {
        List<MeshData> meshes = GltfPrimitiveUtils.modelToMeshList(model);

        for (int i = 0; i < meshes.size(); i++) {
            MeshStorage.addMesh(model.location, meshes.get(i));
            model.meshes.add(meshes.get(i));
            model.meshModelToMeshData.put(meshes.get(i).getMeshModel(), meshes.get(i));

            for (PrimitiveData prim : meshes.get(i).getPrimitives()) {
                MeshStorage.addPrimitive(model.location, prim);
            }
        }
    }

}
