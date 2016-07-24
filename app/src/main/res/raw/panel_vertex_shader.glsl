uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_PerspectiveMatrix;

attribute vec4 a_Position;
attribute vec2 a_TexCoord;

varying vec2 v_texCoord;

void main()
{
    mat4 mv = u_ViewMatrix  * u_ModelMatrix;
    mat4 mvp = u_PerspectiveMatrix * mv;

    v_texCoord = a_TexCoord;

    gl_Position = mvp * a_Position;
}
