precision mediump float;

varying vec3 v_Color;

void main()
{
        vec2 coord = gl_PointCoord - vec2(0.5); //from [0,1] to [-0.5,0.5]
        if(length(coord) > 0.5){
            discard;
        }

        gl_FragColor = vec4(v_Color, 1.0);
}