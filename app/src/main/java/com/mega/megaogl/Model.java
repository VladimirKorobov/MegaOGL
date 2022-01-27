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

    Shader shader;
    public Model(Shader shader) {
        this.shader = shader;

    }

    public void updateLocation() {

    }

    public void draw() {
    }
}
