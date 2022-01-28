package com.mega.megaogl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Model {
    public float[] matrix = new float[16];
    public final float[] currModel = new float[16];

    public Model() {
        Matrix.setIdentityM(matrix, 0);
    }

    public void update() {

    }

    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        Matrix.multiplyMM(currModel, 0, modelMat, 0, matrix, 0);
    }
}
