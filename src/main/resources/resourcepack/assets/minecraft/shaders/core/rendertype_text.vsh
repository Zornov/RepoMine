#version 150
#moj_import <minecraft:fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler2;
uniform mat4 ModelViewMat, ProjMat;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    vec3 pos = Position;
    vec4 color = Color;
    float alpha255 = Color.a * 255.0;

    // Main Color
    if (Color.r == 78.0/255.0 && Color.g == 90.0/255.0) {
        pos.y += Color.b * 255.0;
        color = vec4(1.0);
    }

    vec4 worldPos = vec4(pos, 1.0);
    gl_Position = ProjMat * ModelViewMat * worldPos;
    vertexDistance = fog_distance(pos, FogShape);
    vertexColor = color * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;
}
