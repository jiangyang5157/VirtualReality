#version 300 es
precision mediump float;

uniform vec3 u_Color;
uniform vec3 u_LightPos;

in vec3 v_Position;
in vec3 v_Normal;

out vec4 FragColor;

void main()
{
    vec3 lightVec = normalize(u_LightPos - v_Position);

    float diffuse = max(dot(v_Normal, lightVec), 0.0);

    float distance = length(u_LightPos - v_Position);
    const float diffuseFactor = 0.00025; // 1r:0.00025, 40r:0.01
    diffuse = diffuse * (1.0 / (1.0 + (diffuseFactor * distance * distance)));
    const float minDiffuse = 0.25;
    diffuse = max(diffuse, minDiffuse);

    FragColor = vec4(u_Color, 1.0) * diffuse;
}