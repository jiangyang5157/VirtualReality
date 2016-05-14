precision mediump float;

varying vec3 v_Color;

void main()
{
        vec2 coord = gl_PointCoord - vec2(0.5); //from [0,1] to [-0.5,0.5]
        float length = length(coord);
        if(0.4 < length && length < 0.5){
            gl_FragColor = vec4(v_Color, 1.0);
        } else {
            discard;
        }
}