package com.noodlegamer76.noodleengine.client.glitf.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.animation.AnimationPlayer;
import com.noodlegamer76.noodleengine.event.ShaderRegistry;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.AccessorModel;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL31C.*;

public class SkinUbo {
    private final int uboId;
    private final int maxJoints;
    private final FloatBuffer buffer;
    private final Skin skin;
    private final McGltf model;
    private final List<Matrix4f> inverseBindMatrices;

    public SkinUbo(McGltf model, Skin skin, int nodeCount) {
        this.maxJoints = nodeCount;
        this.skin = skin;
        this.model = model;
        this.buffer = BufferUtils.createFloatBuffer(nodeCount * 16);

        if (skin.getInverseBindMatrices() != null) {
            AccessorModel accessor = model.model.getAccessorModels().get(skin.getInverseBindMatrices());
            float[] floats = com.noodlegamer76.noodleengine.client.glitf.util.GltfAccessorUtils.getFloatArray(accessor);

            List<Matrix4f> list = new ArrayList<>();
            for (int i = 0; i < floats.length; i += 16) {
                Matrix4f m = new Matrix4f().set(
                        floats[i + 0], floats[i + 1], floats[i + 2], floats[i + 3],
                        floats[i + 4], floats[i + 5], floats[i + 6], floats[i + 7],
                        floats[i + 8], floats[i + 9], floats[i + 10], floats[i + 11],
                        floats[i + 12], floats[i + 13], floats[i + 14], floats[i + 15]
                );
                list.add(m);
            }
            this.inverseBindMatrices = list;
        } else {
            this.inverseBindMatrices = new ArrayList<>();
            for (int i = 0; i < skin.getJoints().size(); i++) {
                this.inverseBindMatrices.add(new Matrix4f().identity());
            }
        }

        uboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboId);
        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, (long) nodeCount * 16 * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
    }

    public void bind() {
        RenderSystem.assertOnRenderThread();
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboId);
    }

    public void bindToShader(int bindingPoint) {
        RenderSystem.assertOnRenderThread();
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboId);
        GL31.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, bindingPoint, uboId);
        int blockIndex = glGetUniformBlockIndex(ShaderRegistry.pbr.shader.getId(), "SkinBlock");
        glUniformBlockBinding(ShaderRegistry.pbr.shader.getId(), blockIndex, bindingPoint);
    }

    /**
     * Upload skinning matrices from animation.
     */
    // put this method into SkinUbo (replace existing uploadAnimated)
    public void uploadAnimated(List<Matrix4f> bindLocalPose,
                               Map<Integer, AnimationPlayer.Transform> animTransforms,
                               int[] nodeParents,
                               int skinnedNodeIndex) {
        // ensure render-thread
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> uploadAnimated(bindLocalPose, animTransforms, nodeParents, skinnedNodeIndex));
            return;
        }
        RenderSystem.assertOnRenderThread();
        bind();

        final int nodeCount = bindLocalPose.size();
        List<Matrix4f> local = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            Matrix4f bindLocal = new Matrix4f(bindLocalPose.get(i));
            org.joml.Vector3f baseT = new org.joml.Vector3f();
            org.joml.Quaternionf baseR = new org.joml.Quaternionf();
            org.joml.Vector3f baseS = new org.joml.Vector3f();
            bindLocal.getTranslation(baseT);
            bindLocal.getScale(baseS);
            bindLocal.getNormalizedRotation(baseR);

            AnimationPlayer.Transform sampled = (animTransforms != null) ? animTransforms.get(i) : null;

            org.joml.Vector3f finalT = (sampled != null && sampled.translation != null) ? sampled.translation : baseT;
            org.joml.Quaternionf finalR = (sampled != null && sampled.rotation != null) ? sampled.rotation : baseR;
            org.joml.Vector3f finalS = (sampled != null && sampled.scale != null) ? sampled.scale : baseS;

            Matrix4f composed = new Matrix4f()
                    .translation(finalT.x, finalT.y, finalT.z)
                    .rotate(finalR)
                    .scale(finalS.x, finalS.y, finalS.z);

            local.add(composed);
        }

        List<Matrix4f> globals = new ArrayList<>(Collections.nCopies(nodeCount, null));
        for (int i = 0; i < nodeCount; i++) {
            BindPoseUtils.computeGlobal(i, nodeParents, local, globals);
        }

        Matrix4f meshNodeInv = new Matrix4f().identity();
        if (skinnedNodeIndex >= 0 && skinnedNodeIndex < globals.size()) {
            meshNodeInv = new Matrix4f(globals.get(skinnedNodeIndex)).invert();
        }

        List<Integer> joints = skin.getJoints();
        List<Matrix4f> skinning = new ArrayList<>(joints.size());
        for (int i = 0; i < joints.size(); i++) {
            int jointNodeIndex = joints.get(i);
            if (jointNodeIndex < 0 || jointNodeIndex >= globals.size()) {
                skinning.add(new Matrix4f().identity());
                continue;
            }
            Matrix4f jointGlobal = globals.get(jointNodeIndex);
            Matrix4f invBind = (i < inverseBindMatrices.size()) ? inverseBindMatrices.get(i) : new Matrix4f().identity();

            Matrix4f jointMat = new Matrix4f(meshNodeInv)
                    .mul(jointGlobal)
                    .mul(invBind);
            skinning.add(jointMat);
        }

        uploadSkinningToGpu(skinning);
    }

    /**
     * Upload static skinning matrices (no animation).
     */
    public void upload(List<Matrix4f> nodeGlobalTransforms, int skinnedNodeIndex) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> upload(nodeGlobalTransforms, skinnedNodeIndex));
            return;
        }

        RenderSystem.assertOnRenderThread();
        bind();

        Matrix4f meshNodeInv = new Matrix4f().identity();
        if (skinnedNodeIndex >= 0 && skinnedNodeIndex < nodeGlobalTransforms.size()) {
            meshNodeInv = new Matrix4f(nodeGlobalTransforms.get(skinnedNodeIndex)).invert();
        }

        List<Integer> joints = skin.getJoints();
        List<Matrix4f> skinning = new ArrayList<>(joints.size());

        for (int i = 0; i < joints.size(); i++) {
            int jointNodeIndex = joints.get(i);
            Matrix4f jointGlobal = nodeGlobalTransforms.get(jointNodeIndex);

            Matrix4f jointMat = new Matrix4f(meshNodeInv)
                    .mul(jointGlobal)
                    .mul(inverseBindMatrices.get(i));
            skinning.add(jointMat);
        }

        uploadSkinningToGpu(skinning);
    }

    /**
     * Shared GPU upload for any list of skinning matrices.
     */
    private void uploadSkinningToGpu(List<Matrix4f> skinning) {
        buffer.clear();
        float[] tmp = new float[16];
        for (int i = 0; i < maxJoints; i++) {
            if (i < skinning.size()) {
                skinning.get(i).get(tmp);
            } else {
                new Matrix4f().identity().get(tmp);
            }
            buffer.put(tmp);
        }
        buffer.rewind();
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, buffer);
    }

    public void delete() {
        RenderSystem.assertOnRenderThread();
        GL15.glDeleteBuffers(uboId);
    }

    public int getMaxJoints() { return maxJoints; }
    public int getUboId() { return uboId; }
    public McGltf getModel() { return model; }
    public Skin getSkin() { return skin; }
}
