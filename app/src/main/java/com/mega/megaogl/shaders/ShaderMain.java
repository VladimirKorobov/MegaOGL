package com.mega.megaogl.shaders;

import android.opengl.GLES20;

import com.mega.megaogl.shaders.Shader;

public class ShaderMain extends Shader {
    private static String kLogTag = "GDC11";

    public int mMVPMatrixHandle;
    public int mLightVectorHandle;
    public int mPositionHandle;
    public int mNormalHandle;
    public int mTexCoordHandle;
    public int mNormalMatrixHandle;
    public int mTextureHandle;

    public ShaderMain() {
        super(vertexShader, fragmentShader);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "u_MVPMatrix");
        mLightVectorHandle = GLES20.glGetUniformLocation(mProgramId, "a_LightDir");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramId, "a_Normal");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgramId, "a_TexCoord");
        mNormalMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "u_NormalMatrix");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramId, "u_ShapeTexture");
    }
    public void setLight(float[] lightVector) {
        GLES20.glUniform3fv(mLightVectorHandle, 1, lightVector, 0);
    }

    final static String vertexShader =
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
                    + "   light = max(-dot(v_Normal, a_LightDir) * 1.5f, 0.0f);\n"
                    + "   v_TexCoord = a_TexCoord; \n"
                    + "}\n";
    final static String fragmentShader =
            "varying vec3 v_Normal;\n"
                    + "varying vec2 v_TexCoord;\n"
                    + "uniform sampler2D u_ShapeTexture;\n"
                    + "varying float light;\n"
                    + "void main()\n"
                    + "{\n"
                    + "vec3 col = texture2D(u_ShapeTexture, v_TexCoord).rgb;\n"
                    //+ "vec3 col = vec3(1.0f, 1.0f, 1.0f) * 0.5f;\n"
                    + "gl_FragColor = vec4(light * col, 1.0f);\n"
                    + "}\n";
}
