uniform mat4 u_MVPMatrix;
uniform vec3 u_Color;
uniform float u_PointSize;

attribute vec4 a_Position;

varying vec3 v_Color;
varying float v_PointSize;

void main()
{
    v_Color = u_Color;
    v_PointSize = u_PointSize;

    gl_Position = u_MVPMatrix * a_Position;
    gl_PointSize = u_PointSize;
}
