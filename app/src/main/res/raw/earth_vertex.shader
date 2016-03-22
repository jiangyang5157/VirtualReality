uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec2 a_TexCoords;

varying vec2 v_texCoords;

void main()
{
    v_texCoords = a_TexCoords;

    gl_Position = u_MVPMatrix * a_Position;
}
