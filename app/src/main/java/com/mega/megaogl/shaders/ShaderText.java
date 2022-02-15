package com.mega.megaogl.shaders;

import android.opengl.GLES20;

public class ShaderText extends Shader{
    public ShaderText() {
        super(vertexShader, fragmentShader);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "a_Position");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgramId, "a_TexCoord");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramId, "u_Texture");
    }

    public int mMVPMatrixHandle;
    public int mPositionHandle;
    public int mTexCoordHandle;
    public int mTextureHandle;

    final static String vertexShader =
            "uniform mat4 u_MVPMatrix;\n"
                    + "attribute vec4 a_Position;\n"
                    + "attribute vec2 a_TexCoord;\n"
                    + "varying vec2 v_TexCoord;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   gl_Position = u_MVPMatrix * a_Position;\n"
                    + "   v_TexCoord = a_TexCoord; \n"
                    + "}\n";
    final static String fragmentShader =
            "varying vec2 v_TexCoord;\n"
                    + "uniform sampler2D u_Texture;\n"
                    + "void main()\n"
                    + "{\n"
                    + "float alpha = texture2D(u_Texture, v_TexCoord).a;\n"
                    + "vec4 col = vec4(1.0f, 0.0f, 0.0f, alpha);\n"
                    + "gl_FragColor = col;\n"
                    + "}\n";

}
