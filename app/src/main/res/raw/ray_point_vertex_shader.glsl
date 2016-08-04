#version 300 es

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_PerspectiveMatrix;
uniform float u_PointSize;

in vec4 a_Position;

out float v_PointSize;

void main()
{
    mat4 mv = u_ViewMatrix  * u_ModelMatrix;
    mat4 mvp = u_PerspectiveMatrix * mv;

    gl_Position = mvp * a_Position;

    v_PointSize = u_PointSize;

    gl_PointSize = u_PointSize;
}
