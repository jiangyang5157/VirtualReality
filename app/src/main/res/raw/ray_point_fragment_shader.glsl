#version 300 es
precision mediump float;

uniform vec3 u_Color;
uniform int u_Busy;
uniform float u_Spinner;

in float v_PointSize;

out vec4 FragColor;

void main()
{
    // transform coord from range [0, 1] to [-0.5, 0.5]
    vec2 coord = gl_PointCoord - vec2(0.5);
    // distance from the center
    float length = length(coord);

    float weight = 4.0 / v_PointSize;
    float spinnerUp = 0.4;
    float spinnerDown = spinnerUp - weight * 1.4;
    float pointerUp = spinnerDown - weight * 0.6;
    float pointerDown = (u_Busy == 0) ? pointerUp - weight : 0.0;

    bool belongsToPointer = pointerDown < length && length < pointerUp;
    if (belongsToPointer){
        FragColor = vec4(u_Color, 1.0);
        return;
    }

    bool belongsToSpinner = spinnerDown < length && length < spinnerUp;
    float theta = atan (-coord.x, coord.y); //  (-PI, PI)
    belongsToSpinner = belongsToSpinner && theta < u_Spinner;
    if (belongsToSpinner){
        FragColor = vec4(u_Color, 1.0);
        return;
    }

    discard;
}