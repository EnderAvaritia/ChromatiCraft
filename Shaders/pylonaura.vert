uniform int pylonRed;
uniform int pylonGreen;
uniform int pylonBlue;

void main() {
    vec4 vert = gl_Vertex;   
    gl_Position = gl_ModelViewProjectionMatrix * vert;
    texcoord = vec2(gl_MultiTexCoord0);
}