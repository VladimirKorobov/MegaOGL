package com.mega.megaogl;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Model {
    private float[] matrix = new float[16];
    final int[] vbo = new int[] {0};
    final int[] ibo = new int[] {0};

    public FloatBuffer vertexBuffer;
    public ShortBuffer indicesBuffer;

    Shader shader;
    public Model(Shader shader) {
        this.shader = shader;

        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glGenBuffers(1, ibo, 0);
    }

    public void draw() {
    }
}
