uniform mat4 u_MMatrix;
uniform mat4 u_MVMatrix;
uniform mat4 u_MVPMatrix;

uniform sampler2D u_texId;

attribute vec4 a_Position;
//attribute vec3 a_Normal;
attribute vec2 a_Texture;

varying vec4 v_Color;
varying vec3 v_Position;

void main()
{
    v_Position = vec3(u_MMatrix * a_Position);
    vec4 texColor = texture2D(u_texId, a_Texture);
    v_Color = texColor;
    gl_Position = u_MVPMatrix * a_Position;
}
