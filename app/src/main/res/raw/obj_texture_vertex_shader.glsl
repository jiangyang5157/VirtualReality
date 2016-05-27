uniform mat4 u_MVMatrix;
uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoord;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_texCoord;

void main()
{
    // Transform the vertex into eye space.
    v_Position = vec3(u_MVMatrix * a_Position);

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    v_texCoord = a_TexCoord;

    gl_Position = u_MVPMatrix * a_Position;
}
