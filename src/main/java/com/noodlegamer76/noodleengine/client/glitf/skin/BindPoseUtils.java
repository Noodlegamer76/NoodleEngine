package com.noodlegamer76.noodleengine.client.glitf.skin;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.model.NodeModel;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BindPoseUtils {

    /**
     * Builds global bind-pose matrices for all nodes.
     * These matrices correspond to the node hierarchy and include translation, rotation, and scale.
     */
    public static List<Matrix4f> buildBindPoseGlobals(McGltf model) {
        int nodeCount = model.nodes.size();
        List<Matrix4f> localMatrices = new ArrayList<>(nodeCount);

        for (int i = 0; i < nodeCount; i++) {
            NodeModel node = model.nodes.get(i);
            float[] t = node.getTranslation() != null ? node.getTranslation() : new float[]{0,0,0};
            float[] r = node.getRotation() != null ? node.getRotation() : new float[]{0,0,0,1};
            float[] s = node.getScale() != null ? node.getScale() : new float[]{1,1,1};

            Matrix4f local = new Matrix4f()
                    .translation(t[0], t[1], t[2])
                    .rotate(new Quaternionf(r[0], r[1], r[2], r[3]))
                    .scale(s[0], s[1], s[2]);
            localMatrices.add(local);
        }

        int[] parents = new int[nodeCount];
        Arrays.fill(parents, -1);
        for (int i = 0; i < nodeCount; i++) {
            NodeModel n = model.nodes.get(i);
            if (n.getChildren() != null) {
                for (NodeModel c : n.getChildren()) parents[model.nodes.indexOf(c)] = i;
            }
        }

        List<Matrix4f> globalMatrices = new ArrayList<>(Collections.nCopies(nodeCount, null));
        for (int i = 0; i < nodeCount; i++) {
            computeGlobal(i, parents, localMatrices, globalMatrices);
        }

        for (int i = 0; i < nodeCount; i++) {
            model.bindLocalPose.add(new Matrix4f(localMatrices.get(i)));
        }

        return globalMatrices;
    }


    /**
     * Recursively compute the global transform for a node.
     */
    public static Matrix4f computeGlobal(int index, int[] parents, List<Matrix4f> local, List<Matrix4f> globals) {
        if (globals.get(index) != null) return globals.get(index);

        int parent = parents[index];
        Matrix4f global;
        if (parent == -1) {
            global = new Matrix4f(local.get(index));
        } else {
            global = new Matrix4f(computeGlobal(parent, parents, local, globals))
                    .mul(local.get(index));
        }

        globals.set(index, global);
        return global;
    }

    /**
     * Computes the final skinning matrices for each joint.
     * Multiply: jointGlobal * inverseBind
     * Result is ready to multiply vertex positions (mesh-local space)
     */
    public static List<Matrix4f> buildSkinningMatrices(List<Matrix4f> jointGlobals, List<Matrix4f> inverseBinds) {
        int count = jointGlobals.size();
        List<Matrix4f> skinning = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Matrix4f skin = new Matrix4f(jointGlobals.get(i))
                    .mul(inverseBinds.get(i));
            skinning.add(skin);
        }

        return skinning;
    }
}
