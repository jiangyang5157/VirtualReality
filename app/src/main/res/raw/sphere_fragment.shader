precision mediump float;

uniform sampler2D u_TexId;

varying vec2 v_texCoords;

void main()
{
    // The OpenGL convention (origin at the bottom-left corner) is different than in 2D applications (origin at the top-left corner).
    // To avoid upside-down texture
    vec2 flipped_texcoord = vec2(v_texCoords.x, 1.0 - v_texCoords.y);
    gl_FragColor = texture2D(u_TexId, flipped_texcoord);
}