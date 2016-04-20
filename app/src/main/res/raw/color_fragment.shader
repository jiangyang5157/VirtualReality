precision mediump float;

uniform vec3 u_LightPos;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec4 v_Color;

void main()
{
    vec3 lightVec = normalize(u_LightPos - v_Position);

    float diffuse = max(dot(v_Normal, lightVec), 0.1);

    float distance = length(u_LightPos - v_Position);
    diffuse = diffuse * (1.0 / (1.0 + (0.001 * distance * distance)));

    gl_FragColor = v_Color * diffuse;
}