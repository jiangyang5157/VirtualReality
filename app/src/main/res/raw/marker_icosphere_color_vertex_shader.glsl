#version 300 es

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_PerspectiveMatrix;

in vec4 a_Position;
in vec3 a_Normal;

out vec3 v_Position;
out vec3 v_Normal;

void main()
{
    mat4 mv = u_ViewMatrix * u_ModelMatrix;
    mat4 mvp = u_PerspectiveMatrix * mv;

    // Draw an object that looks the same size regardless of the distance in perspective view.
    const float reciprocalScaleOnScreen = 0.01;
    // Transform the vector <0, 0, 0> to clipspace. This will get the w the object's pivot will be divided by.
    float w = (mvp * vec4(0, 0, 0, 1)).w * reciprocalScaleOnScreen;
    vec4 position = vec4(a_Position.xyz * w , 1);

    // Transform the vertex into eye space.
    v_Position = vec3(mv * position);

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(mv * vec4(a_Normal, 0.0));

    gl_Position = mvp * position;
}
