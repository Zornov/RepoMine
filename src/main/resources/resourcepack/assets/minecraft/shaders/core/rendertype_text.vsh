
#version 150
#moj_import <minecraft:fog.glsl>

in vec3    Position;
in vec4    Color;
in vec2    UV0;
in ivec2   UV2;

uniform sampler2D Sampler2;
uniform mat4      ModelViewMat;
uniform mat4      ProjMat;
uniform int       FogShape;

out float  vertexDistance;
out vec4   vertexColor;
out vec2   texCoord0;


void main() {
    vec4 color = Color;
    
    vec3 pos   = Position;
    ivec4 icol = ivec4(round(Color * 255.0));

    if (Color == vec4(78/255.0, 90/255.0, Color.b, Color.a)) {
        pos.y += Color.b * 255.0 - 50.0;
    }

    gl_Position     = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexDistance  = fog_distance(pos, FogShape);
    vertexColor     = color * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0       = UV0;

}
