precision mediump float;

uniform vec4 u_Color;
uniform vec3 u_LightPos;

varying vec3 v_Position;
varying vec3 v_Normal;

void main()
{
    // Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - v_Position);

    // Calculate the dot product of the light vector and vertex normal.
    // If the normal and light vector are pointing in the same direction then it will get max illumination.
    float diffuse = max(dot(v_Normal, lightVector), 0.5);

    // Add attenuation.
    float distance = length(u_LightPos - v_Position);
    diffuse = diffuse * (1.0 / (1.0 + (0.0001 * distance * distance)));

    // Add ambient lighting
    diffuse = diffuse + 0.2;

    gl_FragColor = u_Color * diffuse;
}