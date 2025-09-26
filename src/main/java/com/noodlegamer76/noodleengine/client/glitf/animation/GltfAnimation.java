package com.noodlegamer76.noodleengine.client.glitf.animation;

import java.util.ArrayList;
import java.util.List;

public class GltfAnimation {
    public String name;
    public final List<AnimationTrack> tracks = new ArrayList<>();

    public static class AnimationTrack {
        public final int nodeIndex;
        public final PathType path;
        public final float[] keyframeTimes;
        public final float[][] values;

        public AnimationTrack(int nodeIndex, PathType path, float[] keyframeTimes, float[][] values) {
            this.nodeIndex = nodeIndex;
            this.path = path;
            this.keyframeTimes = keyframeTimes;
            this.values = values;
        }

        public enum PathType {
            TRANSLATION,
            ROTATION,
            SCALE;

            public static PathType fromString(String str) {
                return switch (str) {
                    case "translation" -> TRANSLATION;
                    case "rotation" -> ROTATION;
                    case "scale" -> SCALE;
                    default -> throw new IllegalArgumentException("Unknown path type: " + str);
                };
            }
        }
    }
}
