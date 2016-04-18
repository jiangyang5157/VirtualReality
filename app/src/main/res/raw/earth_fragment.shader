precision mediump float;

uniform sampler2D u_TexId;

varying vec2 v_texCoord;

void main()
{
    vec2 flipped_texcoord = vec2(1.0 - v_texCoord.x, v_texCoord.y);
    gl_FragColor = texture2D(u_TexId, v_texCoord);
}