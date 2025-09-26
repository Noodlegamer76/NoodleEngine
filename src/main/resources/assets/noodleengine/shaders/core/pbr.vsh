#version 420

#moj_import <light.glsl>

in vec3 Position;
in vec4 Color;
in vec3 Normal;
in vec2 UV0;
in vec2 NormalUV;
in vec2 MetallicUV;
in vec2 RoughnessUV;
in vec2 AoUV;
in vec2 EmissiveUV;
in vec4 JointIndices;
in vec4 JointWeights;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 baseColorFactor;
uniform vec4 emissiveFactor;
uniform sampler2D lightTex;
uniform ivec2 lightUv;
uniform int packedLight;

layout(std140, binding = 0) uniform SkinBlock {
    mat4 joints[1024];
};

out vec2 texCoord0;
out vec2 normalCoord;
out vec2 metallicCoord;
out vec2 roughnessCoord;
out vec2 aoCoord;
out vec2 emissiveCoord;
out vec3 fragViewPos;
out vec3 fragWorldPos;
out vec3 fragNormal;
out vec4 vertexColor;
out vec4 lightColor;

void main() {
    float weightSum = JointWeights.x + JointWeights.y + JointWeights.z + JointWeights.w;

    mat4 skinMat;
    if (weightSum > 0.0) {
        skinMat =
            JointWeights.x * joints[clamp(int(JointIndices.x), 0, 1023)] +
            JointWeights.y * joints[clamp(int(JointIndices.y), 0, 1023)] +
            JointWeights.z * joints[clamp(int(JointIndices.z), 0, 1023)] +
            JointWeights.w * joints[clamp(int(JointIndices.w), 0, 1023)];
    } else {
        skinMat = mat4(1.0);
    }

    vec4 pos = skinMat * vec4(Position, 1.0);
    gl_Position = ProjMat * ModelViewMat * pos;

    mat3 skinNormalMat = mat3(skinMat);
    vec3 skinnedNormal = normalize(skinNormalMat * Normal);

    fragNormal = normalize(skinnedNormal);

    texCoord0 = UV0;
    normalCoord = NormalUV;
    metallicCoord = MetallicUV;
    roughnessCoord = RoughnessUV;
    aoCoord = AoUV;
    emissiveCoord = EmissiveUV;

    lightColor = minecraft_sample_lightmap(lightTex, lightUv);
    vertexColor = Color * baseColorFactor * lightColor;

    fragViewPos = (ModelViewMat * pos).xyz;
    fragWorldPos = pos.xyz;
}
