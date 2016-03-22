precision mediump float;

varying vec4 v_Color;
varying vec3 v_Position;

void main() {
    // Calculate world-space distance.
    float depth = gl_FragCoord.z / gl_FragCoord.w;

    if ((mod(abs(v_Position.x), 10.0) < 0.1) || (mod(abs(v_Position.z), 10.0) < 0.1)) {
        // it is grid line
        gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(0.2470, 0.3176, 0.7098, 1.0)
                + min(1.0, depth / 90.0) * v_Color;
    } else {
        gl_FragColor = v_Color;
    }
}
