package com.mega.megaogl;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {
    private static String kLogTag = "GDC11";

    private int mProgramId;
    public int mMVPMatrixHandle;
    public int mLightVectorHandle;
    public int mPositionHandle;
    public int mNormalHandle;
    public int mTexCoordHandle;
    public int mNormalMatrixHandle;

    public Shader() {
        mProgramId = loadProgram(vertexShader, fragmentShader);
        GLES20.glLinkProgram(mProgramId);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "u_MVPMatrix");
        mLightVectorHandle = GLES20.glGetUniformLocation(mProgramId, "a_LightDir");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramId, "a_Normal");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgramId, "a_TexCoord");
        mNormalMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "u_NormalMatrix");
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
    public void setLight(float[] lightVector) {
        GLES20.glUniform3fv(mLightVectorHandle, 1, lightVector, 0);
    }

    final String vertexShader =
            "uniform mat4 u_MVPMatrix;\n"
                    + "uniform mat3 u_NormalMatrix;\n"
                    + "uniform vec3 a_LightDir;\n"
                    + "attribute vec4 a_Position;\n"
                    + "attribute vec3 a_Normal;\n"
                    + "attribute vec2 a_TexCoord;\n"
                    + "attribute vec4 a_Color;\n"
                    + "varying vec3 v_Normal;\n"
                    + "varying vec2 v_TexCoord;\n"
                    + "varying vec4 v_Color;\n"
                    + "varying float light;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   v_Color = a_Color;\n"
                    + "   gl_Position = u_MVPMatrix * a_Position;\n"
                    + "   v_Normal = u_NormalMatrix * a_Normal;\n"
                    + "   light = max(-dot(v_Normal, a_LightDir), 0.0f);\n"
                    + "   v_TexCoord = a_TexCoord; \n"
                    + "}\n";
    final String fragmentShader =
            "varying vec3 v_Normal;\n"
                    +   "   //varying vec4 v_TexCoord;\n"
                    + "//varying vec4 v_Color;\n"
                    + "varying float light;\n"
                    + "void main()\n"
                    + "{\n"
                    + "vec3 col = vec3(1.0f, 1.0f, 1.0f);\n"
                    + "gl_FragColor = vec4(light * col, 1.0f);\n"
                    + "}\n";
}
