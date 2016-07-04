uniform mat4 u_MVPMatrix;
uniform float u_PointSize;
uniform int u_IsBusy;

attribute vec4 a_Position;

varying float v_PointSize;

void main()
{
    gl_Position = u_MVPMatrix * a_Position;

    v_PointSize = u_PointSize;

    gl_PointSize = u_PointSize;
}
