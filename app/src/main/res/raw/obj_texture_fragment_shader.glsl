precision mediump float;

uniform sampler2D u_TexId;
uniform vec3 u_LightPos;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_texCoord;

void main()
{
    vec3 lightVec = normalize(u_LightPos - v_Position);

    float diffuse = max(dot(v_Normal, lightVec), 0.1);

    float distance = length(u_LightPos - v_Position);
    diffuse = diffuse * (1.0 / (1.0 + (0.0001 * distance * distance)));

    diffuse += 0.9;
    diffuse = min(diffuse, 1.0);

    gl_FragColor = texture2D(u_TexId, v_texCoord) * diffuse;
}