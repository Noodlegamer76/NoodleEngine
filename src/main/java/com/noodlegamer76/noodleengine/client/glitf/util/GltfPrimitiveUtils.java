package com.noodlegamer76.noodleengine.client.glitf.util;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.Vertex;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.model.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

import java.util.*;

public class GltfPrimitiveUtils {

    public static ResourceLocation generateLocation(ResourceLocation parentLoc, ModelElement element, int elementIndex, String folder) {
        return parentLoc.withSuffix(folder + "/" + elementIndex);
    }

    public static List<MeshData> modelToMeshList(McGltf model) {
        List<MeshData> meshes = new ArrayList<>();

        for (int i = 0; i < model.model.getMeshModels().size(); i++) {
            MeshModel meshModel = model.model.getMeshModels().get(i);

            ResourceLocation location = generateLocation(model.location, meshModel, i, "meshes");
            List<PrimitiveData> prims = convertPrimitives(meshModel, location, model);

            MeshData meshData = new MeshData(model, meshModel, prims, location);

            meshes.add(meshData);

            for (PrimitiveData prim : prims) {
                prim.meshData = meshData;
            }
        }

        return meshes;
    }

    public static List<PrimitiveData> convertPrimitives(MeshModel mesh, ResourceLocation meshLoc, McGltf model) {
        List<PrimitiveData> primitives = new ArrayList<>();
        for (int i = 0; i < mesh.getMeshPrimitiveModels().size(); i++) {
            MeshPrimitiveModel prim = mesh.getMeshPrimitiveModels().get(i);
            primitives.add(convertPrimitive(prim, meshLoc, i, model));
        }

        return primitives;
    }

    public static PrimitiveData convertPrimitive(MeshPrimitiveModel prim, ResourceLocation meshLoc, int index, McGltf model) {
        float[] positions = GltfAccessorUtils.getFloatArray(prim.getAttributes().get("POSITION"));
        float[] normals = GltfAccessorUtils.getFloatArray(prim.getAttributes().get("NORMAL"));
        int[] indices = GltfAccessorUtils.getIndexArray(prim.getIndices());

        AccessorModel jointsAccessor = prim.getAttributes().get("JOINTS_0");
        int[] rawJoints = jointsAccessor != null ? GltfAccessorUtils.getJointIndexArray(jointsAccessor) : null;

        AccessorModel weightsAccessor = prim.getAttributes().get("WEIGHTS_0");
        float[] rawWeights = weightsAccessor != null ? GltfAccessorUtils.getFloatArray(weightsAccessor) : null;

        Map<Integer, float[]> uvLayers = new HashMap<>();
        for (Map.Entry<String, AccessorModel> entry : prim.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (key.toUpperCase().startsWith("TEXCOORD")) {
                int layer = Integer.parseInt(key.replace("TEXCOORD_", ""));
                uvLayers.put(layer, GltfAccessorUtils.getFloatArray(entry.getValue()));
            }
        }

        List<Vertex> vertices = new ArrayList<>();
        int vertexCount = positions.length / 3;

        for (int i = 0; i < vertexCount; i++) {
            float x = positions[i * 3];
            float y = positions[i * 3 + 1];
            float z = positions[i * 3 + 2];

            float nx = normals != null ? normals[i * 3] : 0f;
            float ny = normals != null ? normals[i * 3 + 1] : 1f;
            float nz = normals != null ? normals[i * 3 + 2] : 0f;

            // Build UV map for this vertex
            Map<Integer, Vector2f> vertexUVs = new HashMap<>();
            for (Map.Entry<Integer, float[]> uvEntry : uvLayers.entrySet()) {
                int layer = uvEntry.getKey();
                float[] uvArray = uvEntry.getValue();
                vertexUVs.put(layer, new Vector2f(uvArray[i * 2], uvArray[i * 2 + 1]));
            }

            // Build joints
            float[] jointIndices = new float[4];
            if (rawJoints != null) {
                for (int j = 0; j < 4; j++) {
                    jointIndices[j] = rawJoints[i * 4 + j];
                }
            }

            // Build weights
            float[] weights = new float[4];
            if (rawWeights != null) {
                for (int j = 0; j < 4; j++) {
                    int idx = i * 4 + j;
                    weights[j] = idx < rawWeights.length ? rawWeights[idx] : 0f;
                }
            }

            // Normalize weights
            float sum = weights[0] + weights[1] + weights[2] + weights[3];
            if (sum > 0f) {
                for (int j = 0; j < 4; j++) weights[j] /= sum;
            }

            vertices.add(new Vertex(x, y, z, nx, ny, nz, vertexUVs, jointIndices, weights));
        }

        ResourceLocation location = generateLocation(meshLoc, prim, index, "primitives");
        return new PrimitiveData(vertices, indices, location, model, prim);
    }
}
