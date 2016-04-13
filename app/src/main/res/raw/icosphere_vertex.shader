uniform mat4 u_MVMatrix;
uniform mat4 u_MVPMatrix;
uniform vec4 u_Color;
uniform vec3 u_LightPos;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec4 v_Color;

void main()
{
    // Transform the vertex into eye space.
    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);

    // Transform the normal's orientation into eye space.
    vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    // Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);

    // If the normal and light vector are pointing in the same direction then it will get max illumination.
    float diffuse = max(dot(modelViewNormal, lightVector), 0.1);
    float distance = length(u_LightPos - modelViewVertex);
    diffuse = diffuse * (1.0 / (1.0 + (0.001 * distance * distance)));

    v_Color = u_Color * (diffuse + 0.5);

    gl_Position = u_MVPMatrix * a_Position;
}
