package com.noodlegamer76.noodleengine.client.glitf.util;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.Vertex;
import de.javagl.jgltf.impl.v2.Mesh;
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

            Mesh mesh = model.gltf.getMeshes().get(i);

            MeshData meshData = new MeshData(model, mesh, prims, location);

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

        List<Vertex> vertices = new ArrayList<>();
        int vertexCount = positions.length / 3;

        for (int i = 0; i < vertexCount; i++) {
            float x = positions[i * 3];
            float y = positions[i * 3 + 1];
            float z = positions[i * 3 + 2];

            float nx = normals != null ? normals[i * 3] : 0f;
            float ny = normals != null ? normals[i * 3 + 1] : 1f;
            float nz = normals != null ? normals[i * 3 + 2] : 0f;

            Map<Integer, Vector2f> vertexUVs = new HashMap<>();
            for (String key : prim.getAttributes().keySet()) {
                if (key.toUpperCase().startsWith("TEXCOORD")) {
                    float[] uvArray = GltfAccessorUtils.getFloatArray(prim.getAttributes().get(key));
                    vertexUVs.put(Integer.parseInt(key.replaceAll("TEXCOORD_", "")), new Vector2f(uvArray[i*2], uvArray[i*2 + 1]));
                }
            }

            // joints
            float[] jointIndices = new float[] {0, 0, 0, 0};
            AccessorModel jointsAccessor = prim.getAttributes().get("JOINTS_0");
            if (jointsAccessor != null) {
                int[] raw = GltfAccessorUtils.getJointIndexArray(jointsAccessor);
                for (int j = 0; j < 4; j++) {
                    jointIndices[j] = raw[i*4 + j];
                }
            }

            // weights
            float[] weights = new float[4];
            AccessorModel weightsAccessor = prim.getAttributes().get("WEIGHTS_0");
            if (weightsAccessor != null) {
                float[] rawW = GltfAccessorUtils.getFloatArray(weightsAccessor);
                for (int j = 0; j < 4; j++) {
                    int idx = i * 4 + j;
                    weights[j] = idx < rawW.length ? rawW[idx] : 0f;
                }
            } else {
                weights[0] = 1f;
            }

            // normalize weights
            float sum = weights[0] + weights[1] + weights[2] + weights[3];
            if (sum > 0f) {
                for (int j = 0; j < 4; j++) weights[j] /= sum;
            }

            vertices.add(new Vertex(x, y, z, nx, ny, nz, vertexUVs, jointIndices, weights));
        }


        ResourceLocation location = generateLocation(meshLoc, prim, index, "primitives");
        PrimitiveData data = new PrimitiveData(vertices, indices, location, model, prim);
        return data;
    }
}
