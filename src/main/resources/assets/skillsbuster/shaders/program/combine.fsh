#version 150
uniform sampler2D DiffuseSampler;
uniform sampler2D CombineSampler;

in vec2 texCoord;
out vec4 fragColor;

void main(){
    vec4 background = texture(DiffuseSampler,texCoord);
    vec4 combine = texture(CombineSampler,texCoord);
    fragColor = vec4(background.rgb+combine.rgb,1);
}
