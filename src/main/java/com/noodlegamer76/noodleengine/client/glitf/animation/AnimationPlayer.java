package com.noodlegamer76.noodleengine.client.glitf.animation;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple animation player for a single animation.
 * Handles time progression, looping, and interpolation.
 * temporary and will be replaced with a better system later
 */
public class AnimationPlayer {
    private final GltfAnimation animation;
    private float time = 0f;
    private boolean looping = true;

    public AnimationPlayer(GltfAnimation animation) {
        this.animation = animation;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public void reset() {
        time = 0f;
    }

    public void update(float deltaSeconds) {
        time += deltaSeconds;
        float duration = getDuration();
        if (looping) {
            if (duration > 0f) time = time % duration;
        } else {
            if (time > duration) time = duration;
        }
    }

    public float getTime() {
        return time;
    }

    public float getDuration() {
        float max = 0f;
        for (var track : animation.tracks) {
            if (track.keyframeTimes.length > 0) {
                max = Math.max(max, track.keyframeTimes[track.keyframeTimes.length - 1]);
            }
        }
        return max;
    }

    /**
     * Evaluate all tracks at current time.
     * Returns a map: nodeIndex -> local transform (T, R, S).
     */
    public Map<Integer, Transform> sample() {
        Map<Integer, Transform> results = new HashMap<>();
        for (var track : animation.tracks) {
            Transform t = results.computeIfAbsent(track.nodeIndex, i -> new Transform());
            switch (track.path) {
                case TRANSLATION -> t.translation = interpolateVec3(track, time);
                case SCALE -> t.scale = interpolateVec3(track, time);
                case ROTATION -> t.rotation = interpolateQuat(track, time);
            }
        }
        return results;
    }


    // --- interpolation helpers ---

    private Vector3f interpolateVec3(GltfAnimation.AnimationTrack track, float time) {
        int idx = findKeyframeIndex(track.keyframeTimes, time);
        if (idx < 0 || idx >= track.keyframeTimes.length - 1) {
            float[] v = track.values[track.values.length - 1];
            return new Vector3f(v[0], v[1], v[2]);
        }
        float t0 = track.keyframeTimes[idx];
        float t1 = track.keyframeTimes[idx + 1];
        float alpha = (time - t0) / (t1 - t0);

        float[] v0 = track.values[idx];
        float[] v1 = track.values[idx + 1];
        return new Vector3f(
                v0[0] + alpha * (v1[0] - v0[0]),
                v0[1] + alpha * (v1[1] - v0[1]),
                v0[2] + alpha * (v1[2] - v0[2])
        );
    }

    private Quaternionf interpolateQuat(GltfAnimation.AnimationTrack track, float time) {
        int idx = findKeyframeIndex(track.keyframeTimes, time);
        if (idx < 0 || idx >= track.keyframeTimes.length - 1) {
            float[] v = track.values[track.values.length - 1];
            return new Quaternionf(v[0], v[1], v[2], v[3]).normalize();
        }
        float t0 = track.keyframeTimes[idx];
        float t1 = track.keyframeTimes[idx + 1];
        float alpha = (time - t0) / (t1 - t0);

        float[] v0 = track.values[idx];
        float[] v1 = track.values[idx + 1];
        Quaternionf q0 = new Quaternionf(v0[0], v0[1], v0[2], v0[3]);
        Quaternionf q1 = new Quaternionf(v1[0], v1[1], v1[2], v1[3]);
        return q0.slerp(q1, alpha).normalize();
    }

    private int findKeyframeIndex(float[] times, float t) {
        for (int i = 0; i < times.length - 1; i++) {
            if (t >= times[i] && t < times[i + 1]) return i;
        }
        return times.length - 2; // last segment
    }

    /** Container for sampled transforms */
    public static class Transform {
        public Vector3f translation = null;
        public Quaternionf rotation = null;
        public Vector3f scale = null;
    }
}
