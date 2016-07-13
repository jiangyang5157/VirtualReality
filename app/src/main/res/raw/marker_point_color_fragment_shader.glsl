precision mediump float;
uniform vec3 u_Color;

varying vec3 v_Color;
varying float v_PointSize;

void main()
{
    vec2 coord = gl_PointCoord - vec2(0.5); //from [0,1] to [-0.5,0.5]
    float length = length(coord);

    float up = 0.5;
    float down = 0.5 - 4.0f / v_PointSize;

    if(down < length && length < up){
        gl_FragColor = vec4(u_Color, 1.0);
    } else {
        discard;
    }
}