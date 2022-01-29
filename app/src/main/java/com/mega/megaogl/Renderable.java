package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Renderable extends Model {
    private static void getNormalMatrix(float[] model4, float[] normal3) {
        // Create normal matrix
        final float[] inverseMatrix = new float[9];
        final float[] model3 = new float[9];

        Utils.mat3(model4, model3);
        Utils.inverse(model3, inverseMatrix);
        Utils.transpose(inverseMatrix, normal3);
        Utils.normalize(normal3);
    }
    @Override
    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        super.draw(modelMat,viewMat,projectionMat);
        if(viewMat == null || projectionMat == null)
            return;
        final float[] normalMatrix = new float[9];
        final float[] mat = new float[16];
        // Create normal matrix
        getNormalMatrix(currModel, normalMatrix);
        Matrix.multiplyMM(mat, 0, viewMat, 0, currModel, 0);
        Matrix.multiplyMM(mat, 0, projectionMat, 0, mat, 0);
        GLES20.glUniformMatrix4fv(Renderer.shader.mMVPMatrixHandle, 1, false, mat, 0);
        GLES20.glUniformMatrix3fv(Renderer.shader.mNormalMatrixHandle, 1, false, normalMatrix, 0);
    }
}
