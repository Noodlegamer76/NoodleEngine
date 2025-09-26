package com.noodlegamer76.noodleengine.client.glitf.animation;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.util.GltfAccessorUtils;
import de.javagl.jgltf.impl.v2.Animation;
import de.javagl.jgltf.impl.v2.AnimationChannel;
import de.javagl.jgltf.impl.v2.AnimationSampler;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.model.AccessorModel;

import java.util.ArrayList;
import java.util.List;

public class AnimationLoader {

    public static void loadAnimations(McGltf model) {
        if (model.gltf.getAnimations() == null) return;

        for (Animation anim : model.gltf.getAnimations()) {
            GltfAnimation gltfAnim = new GltfAnimation();
            gltfAnim.name = anim.getName();

            for (AnimationChannel channel : anim.getChannels()) {
                AnimationSampler sampler = anim.getSamplers().get(channel.getSampler());
                AccessorModel inputAccessor = model.model.getAccessorModels().get(sampler.getInput());
                AccessorModel outputAccessor = model.model.getAccessorModels().get(sampler.getOutput());

                float[] keyframeTimes = GltfAccessorUtils.getFloatArray(inputAccessor);

                float[][] values;
                switch (channel.getTarget().getPath()) {
                    case "translation", "scale" -> {
                        float[] rawValues = GltfAccessorUtils.getFloatArray(outputAccessor);
                        int count = keyframeTimes.length;
                        values = new float[count][3];
                        for (int i = 0; i < count; i++) {
                            values[i][0] = rawValues[i * 3];
                            values[i][1] = rawValues[i * 3 + 1];
                            values[i][2] = rawValues[i * 3 + 2];
                        }
                    }
                    case "rotation" -> {
                        float[] rawValues = GltfAccessorUtils.getFloatArray(outputAccessor);
                        int count = keyframeTimes.length;
                        values = new float[count][4];
                        for (int i = 0; i < count; i++) {
                            values[i][0] = rawValues[i * 4];
                            values[i][1] = rawValues[i * 4 + 1];
                            values[i][2] = rawValues[i * 4 + 2];
                            values[i][3] = rawValues[i * 4 + 3];
                        }
                    }
                    default -> throw new IllegalStateException("Unsupported animation path: " + channel.getTarget().getPath());
                }

                int nodeIndex = channel.getTarget().getNode();
                gltfAnim.tracks.add(new GltfAnimation.AnimationTrack(nodeIndex,
                        GltfAnimation.AnimationTrack.PathType.fromString(channel.getTarget().getPath()),
                        keyframeTimes,
                        values));
            }

            model.animations.add(gltfAnim);
        }
    }
}
