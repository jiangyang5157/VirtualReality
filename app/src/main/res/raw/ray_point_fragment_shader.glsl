#version 300 es
precision mediump float;

uniform vec3 u_Color;
uniform int u_Busy;
uniform float u_Spinner;

in float v_PointSize;

out vec4 FragColor;

void main()
{
    // Transform coord from range [0, 1] to [-0.5, 0.5]
    vec2 coord = gl_PointCoord - vec2(0.5);
    // The distance to the center
    float length = length(coord);

    float weight = 4.0 / v_PointSize;
    float spinnerUp = 0.4;
    float spinnerDown = spinnerUp - weight * 1.4;
    float pointerUp = spinnerDown - weight * 0.6;
    float pointerDown = (u_Busy == 0) ? pointerUp - weight : 0.0;

    bool isPointer =  pointerDown < length && length < pointerUp;
    if (isPointer){
        FragColor = vec4(u_Color, 1.0);
        return;
    }

    bool isSpinner = spinnerDown < length && length < spinnerUp;
    if (isSpinner){
        // atan returns (-PI, PI)
        isSpinner = atan (coord.y, coord.x) < u_Spinner;
    }

    if (isSpinner){
        FragColor = vec4(u_Color, 1.0);
        return;
    }

    discard;
}