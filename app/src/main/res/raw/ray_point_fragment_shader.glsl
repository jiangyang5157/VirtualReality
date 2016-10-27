#version 300 es
precision mediump float;

uniform vec3 u_Color;
uniform int u_Busy;

in float v_PointSize;

out vec4 FragColor;

void main()
{
    // Transform coord from range [0, 1] to [-0.5, 0.5]
    vec2 coord = gl_PointCoord - vec2(0.5);
    float length = length(coord);

    float up = 0.5;
    float down = 0.0;
    if (u_Busy == 0) {
        down = 0.5 - 4.0f / v_PointSize;
    }

    if (down < length && length < up) {
        FragColor = vec4(u_Color, 1.0);
    } else {
        discard;
    }
}