#version 150
uniform sampler2D DiffuseSampler;
uniform sampler2D CombineSampler;

in vec2 texCoord;
out vec4 fragColor;

void main(){
    vec4 background = texture(DiffuseSampler,texCoord);
    vec4 combine = texture(CombineSampler,texCoord);
    combine.rgb *= combine.a;
    background.rgb *= (1.0 - combine.a);
    fragColor = combine + background;
}
