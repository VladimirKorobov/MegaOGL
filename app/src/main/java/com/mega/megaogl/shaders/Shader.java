package com.mega.megaogl.shaders;

import android.opengl.GLES20;

public class Shader {
    public int mProgramId;

    public Shader(String vertexShader, String fragmentShader) {
        mProgramId = loadProgram(vertexShader, fragmentShader);
        GLES20.glLinkProgram(mProgramId);
    }

    private static int getShader(String source, int type) {
        int shader = GLES20.glCreateShader(type);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = {0};
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        if (shader == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }
        return shader;
    }

    public static int loadProgram(String vertexShader,
                                  String fragmentShader) {
        int vs = getShader(vertexShader, GLES20.GL_VERTEX_SHADER);
        int fs = getShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER);
        if (vs == 0 || fs == 0) return 0;

        int program = GLES20.glCreateProgram();
        if(program != 0) {
            GLES20.glAttachShader(program, vs);
            GLES20.glAttachShader(program, fs);
            GLES20.glLinkProgram(program);

            int[] linked = { 0 };
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0);
            if (linked[0] == 0) {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        if (program == 0)
        {
            throw new RuntimeException("Error creating program.");
        }
        return program;
    }
    public void use() {
        GLES20.glUseProgram(mProgramId);
    }
}
