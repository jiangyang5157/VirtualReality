uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_PerspectiveMatrix;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Position;
varying vec3 v_Normal;

void main()
{
    mat4 mv = u_ViewMatrix  * u_ModelMatrix;
    mat4 mvp = u_PerspectiveMatrix * mv;

    // Transform the vertex into eye space.
    v_Position = vec3(mv * a_Position);

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(mv * vec4(a_Normal, 0.0));

    gl_Position = mvp * a_Position;
}
