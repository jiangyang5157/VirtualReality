uniform mat4 u_MVMatrix;
uniform mat4 u_MVPMatrix;
uniform vec4 u_Color;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec4 v_Color;

void main()
{
    // Transform the vertex into eye space.
    v_Position = vec3(u_MVMatrix * a_Position);

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    v_Color = u_Color;

    gl_Position = u_MVPMatrix * a_Position;
}
