uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec2 a_TexCoord;

varying vec2 v_texCoord;

void main()
{
    v_texCoord = a_TexCoord;

    gl_Position = u_MVPMatrix * a_Position;
}
