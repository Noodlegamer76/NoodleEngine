package com.noodlegamer76.noodleengine.client.glitf.util;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ElementType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GltfAccessorUtils2D {

    public static float[][] getFloatArray2D(AccessorModel accessor) {
        if (accessor == null) return null;

        int count = accessor.getCount();
        int numComponents = getNumComponents(accessor.getElementType());
        float[][] out = new float[count][numComponents];

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
                switch (accessor.getComponentType()) {
                    case 5126 -> out[i][c] = buffer.getFloat(); // FLOAT
                    case 5122 -> { // SHORT
                        short s = buffer.getShort();
                        out[i][c] = accessor.isNormalized() ? (s / (float) Short.MAX_VALUE) : s;
                    }
                    case 5120 -> { // BYTE
                        byte b = buffer.get();
                        out[i][c] = accessor.isNormalized() ? (b / (float) Byte.MAX_VALUE) : b;
                    }
                    case 5123 -> { // UNSIGNED_SHORT
                        int us = buffer.getShort() & 0xFFFF;
                        out[i][c] = accessor.isNormalized() ? (us / (float) 0xFFFF) : us;
                    }
                    case 5121 -> { // UNSIGNED_BYTE
                        int ub = buffer.get() & 0xFF;
                        out[i][c] = accessor.isNormalized() ? (ub / (float) 0xFF) : ub;
                    }
                    default -> throw new IllegalArgumentException("Unsupported componentType: " + accessor.getComponentType());
                }
            }
        }

        return out;
    }

    private static int getNumComponents(ElementType type) {
        return switch (type) {
            case SCALAR -> 1;
            case VEC2 -> 2;
            case VEC3 -> 3;
            case VEC4, MAT2 -> 4;
            case MAT3 -> 9;
            case MAT4 -> 16;
        };
    }

    private static int getComponentSizeBytes(int componentType) {
        return switch (componentType) {
            case 5120, 5121 -> 1; // BYTE, UNSIGNED_BYTE
            case 5122, 5123 -> 2; // SHORT, UNSIGNED_SHORT
            case 5125, 5126 -> 4; // UNSIGNED_INT, FLOAT
            default -> throw new IllegalArgumentException("Unknown componentType: " + componentType);
        };
    }
}
