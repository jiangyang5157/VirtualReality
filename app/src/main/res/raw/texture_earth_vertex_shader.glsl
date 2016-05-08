uniform mat4 u_MVPMatrix;
uniform float u_Radius;

attribute vec3 a_Normal;
attribute vec2 a_TexCoord;

varying vec2 v_texCoord;

void main()
{
    vec4 a_Position = vec4(a_Normal * u_Radius, 1.0);

    v_texCoord = a_TexCoord;

    gl_Position = u_MVPMatrix * a_Position;
}
