uniform mat4 u_ModelMatrix;
uniform mat4 u_MVMatrix;
uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
//attribute vec3 a_Normal;
attribute vec2 a_TexCoords;

varying vec3 v_Position;
varying vec2 v_texCoords;

void main()
{
    v_Position = vec3(u_ModelMatrix * a_Position);

    v_texCoords = a_TexCoords;
    gl_Position = u_MVPMatrix * a_Position;
}
