#version 300 es
precision mediump float;

uniform sampler2D u_TexId;

in vec2 v_texCoord;

out vec4 FragColor;

void main()
{
    FragColor = texture(u_TexId, v_texCoord);
}