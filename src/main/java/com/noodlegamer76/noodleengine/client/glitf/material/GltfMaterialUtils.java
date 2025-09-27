package com.noodlegamer76.noodleengine.client.glitf.material;

import com.mojang.blaze3d.platform.NativeImage;
import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.event.ShaderRegistry;
import de.javagl.jgltf.impl.v2.*;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.v2.MaterialModelV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class GltfMaterialUtils {

    public static Map<Integer, McMaterial> loadMaterials(McGltf model) {
        Map<Integer, McMaterial> result = new HashMap<>();
        Map<ImageModel, ResourceLocation> textureCache = new HashMap<>();



        for (int i = 0; i < model.model.getMaterialModels().size(); i++) {
            MaterialModel mat = model.model.getMaterialModels().get(i);
            if (!(mat instanceof MaterialModelV2)) continue;
            MaterialModelV2 matV2 = (MaterialModelV2) mat;

            String matName = matV2.getName();
            if (matName == null) matName = "gltf_material_" + matV2.hashCode();

            MaterialBuilder builder = new MaterialBuilder(ShaderRegistry.pbr, matName);

            // Base color
            TextureModel baseColor = matV2.getBaseColorTexture();
            if (baseColor != null) {
                Integer texCoord = matV2.getBaseColorTexcoord();
                ResourceLocation tex = loadTexture(baseColor, textureCache);
                builder.set(MaterialProperty.ALBEDO_MAP, tex);
                builder.setTexCoord(MaterialProperty.ALBEDO_MAP, texCoord == null ? 0 : texCoord);
            }

            // Metallic + Roughness
            TextureModel mr = matV2.getMetallicRoughnessTexture();
            if (mr != null) {
                Integer texCoord = matV2.getMetallicRoughnessTexcoord();
                ResourceLocation tex = loadTexture(mr, textureCache);
                builder.set(MaterialProperty.METALLIC_MAP, tex);
                builder.set(MaterialProperty.ROUGHNESS_MAP, tex);
                builder.setTexCoord(MaterialProperty.METALLIC_MAP, texCoord == null ? 0 : texCoord);
            }

            // Normal map
            TextureModel normal = matV2.getNormalTexture();
            if (normal != null) {
                Integer texCoord = matV2.getNormalTexcoord();
                ResourceLocation tex = loadTexture(normal, textureCache);
                builder.set(MaterialProperty.NORMAL_MAP, tex);
                builder.setTexCoord(MaterialProperty.NORMAL_MAP, texCoord == null ? 0 : texCoord);
            }

            // Occlusion map
            TextureModel ao = matV2.getOcclusionTexture();
            if (ao != null) {
                Integer texCoord = matV2.getOcclusionTexcoord();
                ResourceLocation tex = loadTexture(ao, textureCache);
                builder.set(MaterialProperty.AO_MAP, tex);
                builder.setTexCoord(MaterialProperty.AO_MAP, texCoord == null ? 0 : texCoord);
            }

            // Emissive map
            TextureModel emissive = matV2.getEmissiveTexture();
            if (emissive != null) {
                Integer texCoord = matV2.getEmissiveTexcoord();
                ResourceLocation tex = loadTexture(emissive, textureCache);
                builder.set(MaterialProperty.EMISSIVE_MAP, tex);
                builder.setTexCoord(MaterialProperty.EMISSIVE_MAP, texCoord == null ? 0 : texCoord);
            }

            // Transparency
            //builder.transparent(MaterialAlphaMode.isBlend(matV2.getAlphaMode()));

            builder.set(MaterialProperty.AO, matV2.getOcclusionStrength());

            float[] baseColorFactor = matV2.getBaseColorFactor();
            Vector4f color;
            if (baseColorFactor == null) color = new Vector4f(1, 1, 1, 1);
            else color = new Vector4f(baseColorFactor[0], baseColorFactor[1], baseColorFactor[2], baseColorFactor[3]);
            builder.set(MaterialProperty.BASE_COLOR_FACTOR, color);

            float[] emissiveFactor = matV2.getEmissiveFactor();
            Vector4f emissiveColor;
            if (emissiveFactor == null) emissiveColor = new Vector4f(0, 0, 0, 1);
            else emissiveColor = new Vector4f(emissiveFactor[0], emissiveFactor[1], emissiveFactor[2], 1);
            builder.set(MaterialProperty.EMISSIVE_FACTOR, emissiveColor);

            result.put(i, builder.build());
        }

        return result;
    }

    public static void assignMaterials(McGltf model, Map<Integer, McMaterial> materials) {
        for (int i = 0; i < model.meshes.size(); i++) {
            MeshData meshData = model.meshes.get(i);

            for (int j = 0; j < meshData.getPrimitives().size(); j++) {
                PrimitiveData primitiveData = meshData.getPrimitives().get(j);

                MaterialModel matModel = model.model
                        .getMeshModels().get(i)
                        .getMeshPrimitiveModels().get(j)
                        .getMaterialModel();

                if (matModel == null) continue;

                String matName = matModel.getName();
                if (matName == null) matName = "gltf_material_" + matModel.hashCode();

                McMaterial mcMat = materials.get(model.model.getMaterialModels().indexOf(matModel));
                if (mcMat == null) continue;

                primitiveData.setMaterial(mcMat);
            }
        }
    }

    private static ResourceLocation loadTexture(TextureModel texModel, Map<ImageModel, ResourceLocation> cache) {
        ImageModel img = texModel.getImageModel();
        if (img == null) return null;
        return cache.computeIfAbsent(img, GltfMaterialUtils::createTextureFromImageModel);
    }

    private static ResourceLocation createTextureFromImageModel(ImageModel imgModel) {
        String name = imgModel.getName();
        if (name == null) name = "gltf_texture_" + imgModel.hashCode();
        name = name.replaceAll("[^a-zA-Z0-9_]", "_");

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                NoodleEngine.MODID, UUID.randomUUID() + "_" + name
        );

        ByteBuffer data = imgModel.getImageData();
        byte[] bytes;
        if (data == null) bytes = new byte[0];
        else {
            data.rewind();
            bytes = new byte[data.remaining()];
            data.get(bytes);
        }

        Minecraft.getInstance().execute(() -> {
            try {
                TextureManager manager = Minecraft.getInstance().getTextureManager();
                NativeImage image;

                if (bytes.length == 0) {
                    image = new NativeImage(1, 1, false);
                    image.setPixelRGBA(0, 0, 0xFFFF00FF);
                } else {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                        image = NativeImage.read(bais);
                    }
                }

                DynamicTexture tex = new DynamicTexture(image);
                manager.register(location, tex);
                tex.upload();

            } catch (Throwable t) {
                throw new RuntimeException("Could not load texture " + location, t);
            }
        });

        return location;
    }
}
