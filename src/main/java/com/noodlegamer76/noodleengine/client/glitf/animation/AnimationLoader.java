package com.noodlegamer76.noodleengine.client.glitf.animation;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.util.GltfAccessorUtils;
import de.javagl.jgltf.impl.v2.Animation;
import de.javagl.jgltf.impl.v2.AnimationChannel;
import de.javagl.jgltf.impl.v2.AnimationSampler;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.NodeModel;

import java.util.ArrayList;
import java.util.List;

public class AnimationLoader {
    public static void loadAnimations(McGltf model) {
        if (model.model.getAnimationModels() == null || model.model.getAnimationModels().isEmpty()) return;

        List<NodeModel> nodeModels = model.model.getNodeModels();
        java.util.Map<NodeModel, Integer> nodeIndexMap = new java.util.HashMap<>();
        for (int i = 0; i < nodeModels.size(); i++) nodeIndexMap.put(nodeModels.get(i), i);

        for (AnimationModel anim : model.model.getAnimationModels()) {
            GltfAnimation gltfAnim = new GltfAnimation();
            gltfAnim.name = anim.getName();

            for (AnimationModel.Channel channel : anim.getChannels()) {
                AnimationModel.Sampler sampler = channel.getSampler();
                AccessorModel inputAccessor = sampler.getInput();
                AccessorModel outputAccessor = sampler.getOutput();

                float[] keyframeTimes = GltfAccessorUtils.getFloatArray(inputAccessor);
                float[] rawValues = GltfAccessorUtils.getFloatArray(outputAccessor);

                int count = keyframeTimes.length;
                float[][] values;

                switch (channel.getPath()) {
                    case "translation", "scale" -> {
                        values = new float[count][3];
                        for (int i = 0, j = 0; i < count; i++, j += 3)
                            System.arraycopy(rawValues, j, values[i], 0, 3);
                    }
                    case "rotation" -> {
                        values = new float[count][4];
                        for (int i = 0, j = 0; i < count; i++, j += 4)
                            System.arraycopy(rawValues, j, values[i], 0, 4);
                    }
                    default -> throw new IllegalStateException("Unsupported animation path: " + channel.getPath());
                }

                NodeModel nodeModel = channel.getNodeModel();
                Integer nodeIndex = nodeIndexMap.get(nodeModel);
                if (nodeIndex == null) throw new IllegalStateException("Node not found in model");

                gltfAnim.tracks.add(new GltfAnimation.AnimationTrack(
                        nodeIndex,
                        GltfAnimation.AnimationTrack.PathType.fromString(channel.getPath()),
                        keyframeTimes,
                        values
                ));
            }

            model.animations.add(gltfAnim);
        }
    }

}
