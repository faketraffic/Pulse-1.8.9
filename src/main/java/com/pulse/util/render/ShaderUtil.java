package com.pulse.util.render;

import org.lwjgl.opengl.GL20;

public class ShaderUtil {

    public static int createProgram(String vertexSource, String fragmentSource) {
        int program = GL20.glCreateProgram();
        int vert = compileShader(vertexSource, GL20.GL_VERTEX_SHADER);
        int frag = compileShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(program, vert);
        GL20.glAttachShader(program, frag);
        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("[Pulse] Shader link error: " + GL20.glGetProgramInfoLog(program, 1024));
            GL20.glDeleteProgram(program);
            return -1;
        }

        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        return program;
    }

    private static int compileShader(String source, int type) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("[Pulse] Shader compile error: " + GL20.glGetShaderInfoLog(shader, 1024));
        }
        return shader;
    }

    public static final String PASSTHROUGH_VERTEX =
            "#version 120\n" +
            "void main() {\n" +
            "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
            "}\n";

    public static final String KAWASE_FRAGMENT =
            "#version 120\n" +
            "uniform sampler2D inTexture;\n" +
            "uniform vec2 texelSize;\n" +
            "uniform float offset;\n" +
            "void main() {\n" +
            "    vec2 uv = gl_TexCoord[0].st;\n" +
            "    vec4 sum = texture2D(inTexture, uv) * 4.0;\n" +
            "    sum += texture2D(inTexture, uv + vec2(-texelSize.x, -texelSize.y) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2( texelSize.x, -texelSize.y) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2(-texelSize.x,  texelSize.y) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2( texelSize.x,  texelSize.y) * offset);\n" +
            "    gl_FragColor = sum / 8.0;\n" +
            "}\n";
}
