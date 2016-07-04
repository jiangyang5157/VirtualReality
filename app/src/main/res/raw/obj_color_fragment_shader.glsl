precision mediump float;
uniform vec3 u_Color;

uniform vec3 u_LightPos;

varying vec3 v_Position;
varying vec3 v_Normal;

void main()
{
    vec3 lightVec = normalize(u_LightPos - v_Position);

    float diffuse = max(dot(v_Normal, lightVec), 0.0);

    float distance = length(u_LightPos - v_Position);
    const float diffuseFactor = 0.0005;
    diffuse = diffuse * (1.0 / (1.0 + (diffuseFactor * distance * distance)));
    const float minDiffuse = 0.25;
    diffuse = max(diffuse, minDiffuse);

    gl_FragColor = vec4(u_Color, 1.0) * diffuse;
}