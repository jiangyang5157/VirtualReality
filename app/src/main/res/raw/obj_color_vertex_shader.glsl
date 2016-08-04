#version 300 es

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_PerspectiveMatrix;

in vec4 a_Position;
in vec3 a_Normal;

out vec3 v_Position;
out vec3 v_Normal;

void main()
{
    mat4 mv = u_ViewMatrix * u_ModelMatrix;
    mat4 mvp = u_PerspectiveMatrix * mv;

    // Transform the vertex into eye space.
    v_Position = vec3(mv * a_Position);

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(mv * vec4(a_Normal, 0.0));

    gl_Position = mvp * a_Position;
}
