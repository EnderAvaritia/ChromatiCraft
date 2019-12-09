uniform float distance;

uniform float scale;
uniform float clip;

uniform float dx;
uniform float dy;
uniform float dz;

void main() {
	vec2 particleXY = getScreenPos(dx, dy, dz);	
	float distv = distsq(particleXY, texcoord);
	float distfac_vertex = max(0.0, min(1.0, clip-15.0*distv*distance/scale));
	float vf = intensity*distfac_vertex;
	
	texcoord = mix(texcoord, particleXY, vf);
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	//color.rgb = mix(color.rgb, vec3(1.0, 0.0, 0.0), vf);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}