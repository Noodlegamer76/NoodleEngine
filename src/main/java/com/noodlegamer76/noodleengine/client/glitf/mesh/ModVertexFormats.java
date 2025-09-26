package com.noodlegamer76.noodleengine.client.glitf.mesh;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class ModVertexFormats {
    public static final VertexFormatElement ELEMENT_NORMAL_UV = new VertexFormatElement(3, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement ELEMENT_METALLIC_UV = new VertexFormatElement(4, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement ELEMENT_ROUGHNESS_UV = new VertexFormatElement(5, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement ELEMENT_AO_UV = new VertexFormatElement(6, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement ELEMENT_EMISSIVE_UV = new VertexFormatElement(7, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement JOINTS = new VertexFormatElement(8, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 4);
    public static final VertexFormatElement WEIGHTS = new VertexFormatElement(9, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 4);


    public static final VertexFormat GLB_PBR = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder()
                    // stock
                    .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
                    .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
                    .put("UV0", DefaultVertexFormat.ELEMENT_UV0)
                    .put("Normal", DefaultVertexFormat.ELEMENT_NORMAL)

                    .put("NormalUV", ELEMENT_NORMAL_UV)
                    .put("MetallicUV", ELEMENT_METALLIC_UV)
                    .put("RoughnessUV", ELEMENT_ROUGHNESS_UV)
                    .put("AoUV", ELEMENT_AO_UV)
                    .put("EmissiveUV", ELEMENT_EMISSIVE_UV)
                    .put("JointIndices", JOINTS)
                    .put("JointWeights", WEIGHTS)
                    .build()
    );

}
