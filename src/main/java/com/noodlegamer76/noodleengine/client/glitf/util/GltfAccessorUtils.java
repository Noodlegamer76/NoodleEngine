package com.noodlegamer76.noodleengine.client.glitf.util;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ElementType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GltfAccessorUtils {

    public static float[] getFloatArray(AccessorModel accessor) {
        if (accessor == null) return null;

        int count = accessor.getCount();
        int numComponents = getNumComponents(accessor.getElementType());
        float[] out = new float[count * numComponents];

        BufferViewModel view = accessor.getBufferViewModel();
        if (view == null) return null;

        ByteBuffer buffer = view.getBufferViewData().duplicate().order(ByteOrder.LITTLE_ENDIAN);
        int accessorOffset = accessor.getByteOffset();

        int elementSize = numComponents * getComponentSizeBytes(accessor.getComponentType());
        int stride = (view.getByteStride() != null && view.getByteStride() > 0)
                ? view.getByteStride()
                : elementSize;

        if (accessorOffset + (count - 1) * stride + elementSize > buffer.limit()) {
            throw new IllegalStateException("Accessor exceeds buffer limit: " + accessor);
        }

        for (int i = 0; i < count; i++) {
            int elementPos = accessorOffset + i * stride;
            buffer.position(elementPos);

            for (int c = 0; c < numComponents; c++) {
                int idx = i * numComponents + c;
                switch (accessor.getComponentType()) {
                    case 5126 -> out[idx] = buffer.getFloat(); // FLOAT
                    case 5122 -> { // SHORT
                        short s = buffer.getShort();
                        out[idx] = accessor.isNormalized() ? (s / (float) Short.MAX_VALUE) : s;
                    }
                    case 5120 -> { // BYTE
                        byte b = buffer.get();
                        out[idx] = accessor.isNormalized() ? (b / (float) Byte.MAX_VALUE) : b;
                    }
                    case 5123 -> { // UNSIGNED_SHORT
                        int us = buffer.getShort() & 0xFFFF;
                        out[idx] = accessor.isNormalized() ? (us / (float) 0xFFFF) : us;
                    }
                    case 5121 -> { // UNSIGNED_BYTE
                        int ub = buffer.get() & 0xFF;
                        out[idx] = accessor.isNormalized() ? (ub / (float) 0xFF) : ub;
                    }
                    default -> throw new IllegalArgumentException("Unsupported componentType: " + accessor.getComponentType());
                }
            }
        }

        return out;
    }



    public static int[] getIndexArray(AccessorModel accessor) {
        if (accessor == null) return null;

        int count = accessor.getCount();
        int[] out = new int[count];

        BufferViewModel view = accessor.getBufferViewModel();
        if (view == null) return null;

        ByteBuffer buffer = view.getBufferViewData().duplicate().order(ByteOrder.LITTLE_ENDIAN);
        int accessorOffset = accessor.getByteOffset();

        int elementSize = getComponentSizeBytes(accessor.getComponentType());
        int stride = (view.getByteStride() != null && view.getByteStride() > 0)
                ? view.getByteStride()
                : elementSize;

        if (accessorOffset + (count - 1) * stride + elementSize > buffer.limit()) {
            throw new IllegalStateException("Accessor exceeds buffer limit: " + accessor);
        }

        for (int i = 0; i < count; i++) {
            int elementPos = accessorOffset + i * stride;
            buffer.position(elementPos);

            switch (accessor.getComponentType()) {
                case 5121 -> out[i] = buffer.get() & 0xFF; // UNSIGNED_BYTE
                case 5123 -> out[i] = buffer.getShort() & 0xFFFF; // UNSIGNED_SHORT
                case 5125 -> out[i] = buffer.getInt(); // UNSIGNED_INT
                default -> throw new IllegalArgumentException("Unsupported index component type: " + accessor.getComponentType());
            }
        }

        return out;
    }

    public static int[] getJointIndexArray(AccessorModel accessor) {
        if (accessor == null) return null;

        int count = accessor.getCount() * getNumComponents(accessor.getElementType());
        int[] out = new int[count];

        ByteBuffer buffer = accessor.getBufferViewModel().getBufferViewData().duplicate().order(ByteOrder.LITTLE_ENDIAN);
        int offset = accessor.getByteOffset();
        int stride = accessor.getBufferViewModel().getByteStride() != null ? accessor.getBufferViewModel().getByteStride() : getNumComponents(accessor.getElementType()) * getComponentSizeBytes(accessor.getComponentType());

        for (int i = 0; i < accessor.getCount(); i++) {
            buffer.position(offset + i * stride);
            for (int c = 0; c < getNumComponents(accessor.getElementType()); c++) {
                int idx = i * getNumComponents(accessor.getElementType()) + c;
                switch (accessor.getComponentType()) {
                    case 5121 -> out[idx] = buffer.get() & 0xFF; // UNSIGNED_BYTE
                    case 5123 -> out[idx] = buffer.getShort() & 0xFFFF; // UNSIGNED_SHORT
                    default -> throw new IllegalArgumentException("Unsupported joint component type: " + accessor.getComponentType());
                }
            }
        }
        return out;
    }

    public static int getNumComponents(ElementType type) {
        return switch (type) {
            case SCALAR -> 1;
            case VEC2 -> 2;
            case VEC3 -> 3;
            case VEC4, MAT2 -> 4;
            case MAT3 -> 9;
            case MAT4 -> 16;
        };
    }

    public static int getComponentSizeBytes(int componentType) {
        return switch (componentType) {
            case 5120, 5121 -> 1; // BYTE, UNSIGNED_BYTE
            case 5122, 5123 -> 2; // SHORT, UNSIGNED_SHORT
            case 5125, 5126 -> 4; // UNSIGNED_INT, FLOAT
            default -> throw new IllegalArgumentException("Unknown componentType: " + componentType);
        };
    }
}
