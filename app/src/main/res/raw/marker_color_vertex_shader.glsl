uniform mat4 u_MVMatrix;
uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Position;
varying vec3 v_Normal;

void main()
{
    const float reciprocalScaleOnScreen = 0.01;
    float w = (u_MVPMatrix * vec4(0,0,0,1)).w * reciprocalScaleOnScreen;
    vec4 pos = vec4(a_Position.xyz * w , 1);

    // Transform the vertex into eye space.
    v_Position = vec3(u_MVMatrix * a_Position);

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    gl_Position = u_MVPMatrix * pos;
}
