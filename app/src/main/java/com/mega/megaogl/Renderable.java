package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Renderable extends Model {
    static Shader shader;
    private static void getNormalMatrix(float[] model4, float[] normal3) {
        // Create normal matrix
        final float[] inverseMatrix = new float[9];
        final float[] model3 = new float[9];

        Utils.mat3(model4, model3);
        Utils.inverse(model3, inverseMatrix);
        Utils.transpose(inverseMatrix, normal3);
        Utils.normalize(normal3);
    }
    public static void updateShader(Shader shader_) {
        shader = shader_;
    }
    @Override
    public void update() {
        super.update();
    }
    @Override
    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        super.draw(modelMat,viewMat,projectionMat);
        final float[] normalMatrix = new float[9];
        // Create normal matrix
        getNormalMatrix(currModel, normalMatrix);
        Matrix.multiplyMM(currModel, 0, viewMat, 0, currModel, 0);
        Matrix.multiplyMM(currModel, 0, projectionMat, 0, currModel, 0);
        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, currModel, 0);
        GLES20.glUniformMatrix3fv(shader.mNormalMatrixHandle, 1, false, normalMatrix, 0);
    }
}
