#version 150
uniform sampler2D DiffuseSampler;
uniform sampler2D WarpImage;
uniform float iTime;

in vec2 texCoord;
out vec4 fragColor;

float random( vec2 pos )
{
    return fract(sin(dot(pos, vec2(12.9898,78.233))) * 43758.5453);
}

float noise( vec2 pos )
{
    return random( floor( pos ) );
}

float value_noise( vec2 pos )
{
    vec2 p = floor( pos );
    vec2 f = fract( pos );

    float v00 = noise( p + vec2( 0.0, 0.0 ) );
    float v10 = noise( p + vec2( 1.0, 0.0 ) );
    float v01 = noise( p + vec2( 0.0, 1.0 ) );
    float v11 = noise( p + vec2( 1.0, 1.0 ) );

    vec2 u = f * f * ( 3.0 - 2.0 * f );

    return mix( mix( v00, v10, u.x ), mix( v01, v11, u.x ), u.y );
}

void main(){
    vec4 warpBG = texture(WarpImage, texCoord);
    vec2 uv_r = texCoord + vec2(cos(iTime/4)/4, sin(iTime/4)/6);
    vec2 uv_g = texCoord + vec2(sin(iTime/2)/8, cos(iTime/6)/4);
    vec2 shift = vec2(
    sin(
    value_noise(uv_r * 8.0) * 0.2
    +   value_noise(uv_r * 16.0) * 0.2
    +   value_noise(uv_r * 32.0) * 0.2
    +   value_noise(uv_r * 64.0) * 0.2
    +   value_noise(uv_r * 96.0) * 0.2
    ) - 0.5
    , sin(
    value_noise(uv_g * 8.0) * 0.2
    +   value_noise(uv_g * 16.0) * 0.2
    +   value_noise(uv_g * 32.0) * 0.2
    +   value_noise(uv_g * 64.0) * 0.2
    +   value_noise(uv_g * 96.0) * 0.2
    ) - 0.5
    ) * 0.1;
    vec4 diff = texture(DiffuseSampler, texCoord + shift * 0.2);


    fragColor = diff;
}
