precision mediump float;

uniform sampler2D u_TexId;

varying vec2 v_texCoord;

void main()
{
    // To avoid upside-down texture - OpenGL convention (origin at the bottom-left corner)
    vec2 flipped_texcoord = vec2(v_texCoord.x, 1.0 - v_texCoord.y);

    gl_FragColor = texture2D(u_TexId, flipped_texcoord);
}