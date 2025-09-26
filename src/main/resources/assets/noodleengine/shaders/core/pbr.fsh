#version 150

uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;
uniform sampler2D emissiveMap;
uniform sampler2D depthTex;

uniform float metallic;
uniform float roughness;
uniform float ao;
uniform vec4 baseColorFactor;
uniform vec4 emissiveFactor;
uniform mat4 projMatInverse;
uniform mat4 modelViewMatInverse;
uniform vec3 cameraPos;
uniform vec2 ScreenSize;

// Hardcoded directional light
const vec3 lightDir = normalize(vec3(0.15, 1.0, 0.1));

in vec2 texCoord0;
in vec2 normalCoord;
in vec2 metallicCoord;
in vec2 roughnessCoord;
in vec2 aoCoord;
in vec2 emissiveCoord;
in vec3 fragViewPos;
in vec3 fragWorldPos;
in vec3 fragNormal;
in vec4 vertexColor;
in vec4 lightColor;

out vec4 fragColor;

const float PI = 3.14159265359;

vec3 applyNormalMap(vec3 N, vec3 T, vec3 B, vec3 normalSample) {
    vec3 mappedNormal = normalize(normalSample * 2.0 - 1.0);
    return normalize(mappedNormal.x * T + mappedNormal.y * B + mappedNormal.z * N);
}

void main() {
    vec3 albedo = texture(albedoMap, texCoord0).rgb * baseColorFactor.rgb * vertexColor.rgb;

    float m = clamp(metallic * texture(metallicMap, metallicCoord).r, 0.0, 1.0);
    float r = clamp(roughness * texture(roughnessMap, roughnessCoord).r, 0.04, 1.0);

    // AO sample & factor (soft application to directional light)
    float aoVal = clamp(ao * texture(aoMap, aoCoord).r, 0.0, 1.0);
    float aoFactor = mix(1.0, aoVal, 0.5);

    vec3 N = normalize(fragNormal);
    vec3 T = normalize(vec3(1.0,0.0,0.0));
    T = normalize(T - N * dot(N,T));
    vec3 B = normalize(cross(N,T));
    // normalMap currently not applied in this shader version, applyNormalMap kept available
    // N = applyNormalMap(N, T, B, texture(normalMap, normalCoord).rgb);

    vec3 L = normalize(lightDir);
    float NdotL = max(dot(N, L), 0.0);

    // global (full) + directional (AO-modulated) diffuse
    vec3 globalDiffuse = albedo * lightColor.xyz;
    vec3 dirDiffuse = albedo * NdotL * aoFactor;
    vec3 diffuse = globalDiffuse + dirDiffuse;

    // specular (kept as before)
    vec3 R = reflect(-L, N);
    float specPower = mix(8.0, 64.0, 1.0 - r); // sharper for low roughness
    float specFactor = pow(max(dot(R, L), 0.0), specPower);
    vec3 specular = lightColor.xyz * m * 0.5 * specFactor;

    // ambient (AO applied)
    vec3 ambient = albedo * 0.05 * aoVal;

    vec3 emissive = texture(emissiveMap, emissiveCoord).rgb * emissiveFactor.rgb;

    vec3 color = ambient + diffuse + specular + emissive;
    fragColor = vec4(color, baseColorFactor.a * vertexColor.a);
}
