#version 150
uniform sampler2D DiffuseSampler;
uniform sampler2D CombineSampler;
uniform sampler2D RemoveSampler;

in vec2 texCoord;
out vec4 fragColor;

void main(){
    vec4 background = texture(CombineSampler,texCoord);
    vec4 remove = texture(RemoveSampler,texCoord)*0.5;
    vec4 combine = texture(DiffuseSampler,texCoord)-remove;
    fragColor = vec4(background.rgb+combine.rgb,1);
}
