package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;

public class SphereModel extends Renderable{
    Texture texture;
    static float[][] vertices = new float[1][];
    static short[][] indices = new short[1][];

    static int[] vbo = new int[] {0};
    static int[] ibo = new int[] {0};

    static FloatBuffer vertexBuffer;
    static ShortBuffer indicesBuffer;

    public static int sphereLevel = 16;
    private int texId;

    public SphereModel(int texId) {
        this.texId = texId;
        /*
        earth = new Texture(
                R.drawable.forest_posz512,
                R.drawable.forest_posx512,
                R.drawable.forest_negz512,
                R.drawable.forest_negx512,
                R.drawable.forest_posy512,
                R.drawable.forest_negy512);

         */
        /*
        earth = new Texture(
                R.drawable.earth_posz_512,
                R.drawable.earth_posx_512,
                R.drawable.earth_negz_512,
                R.drawable.earth_negx_512,
                R.drawable.earth_posy_512,
                R.drawable.earth_negy_512);

         */

    }
    public static void createBuffers() {
        if(vertices[0] == null)
            Utils.CalcSphere(vertices, indices, sphereLevel);

        vbo[0] = 0;
        ibo[0] = 0;
        GLES20.glGenBuffers(1, vbo, 0);
        int error = GLES20.glGetError();
        GLES20.glGenBuffers(1, ibo, 0);
        error = GLES20.glGetError();

        vertexBuffer = ByteBuffer.allocateDirect(vertices[0].length * Renderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        error = GLES20.glGetError();
        vertexBuffer.put(vertices[0]).position(0);
        error = GLES20.glGetError();

        indicesBuffer = ByteBuffer.allocateDirect(indices[0].length * Renderer.mBytesPerShort)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        error = GLES20.glGetError();
        indicesBuffer.put(indices[0]).position(0);
        error = GLES20.glGetError();

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        error = GLES20.glGetError();
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity()
                * Renderer.mBytesPerFloat, vertexBuffer, GLES20.GL_STATIC_DRAW);
        error = GLES20.glGetError();

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity()
                * Renderer.mBytesPerShort, indicesBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    @Override
    public void update() {
        super.update();
        texture = null;
    }

    @Override
    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        super.draw(modelMat, viewMat, projectionMat);
        if(texture == null) {
            texture = new Texture(texId);
        }
        int offset = 0;
        int stride = (3 + 3 + 2) * Renderer.mBytesPerFloat;
        String s;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        int error = GLES20.glGetError();
        int l = GLES20.glGetUniformLocation(shader.mProgramId, "u_MVPMatrix");
        if(l != shader.mMVPMatrixHandle)
            shader.mMVPMatrixHandle = l;

        if(error != 0) {
            error = GLES20.glGetError();
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

        error = GLES20.glGetError();
        if(error != 0) {
            createBuffers();
            error = GLES20.glGetError();
        }

        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);
        error = GLES20.glGetError();
        GLES20.glVertexAttribPointer(shader.mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        error = GLES20.glGetError();
        offset += 3 * Renderer.mBytesPerFloat;

        GLES20.glVertexAttribPointer(shader.mNormalHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(shader.mNormalHandle);

        // Texture
        offset += 3 * Renderer.mBytesPerFloat;

        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, texture.textureIds[0]);
        GLES20.glUniform1i(shader.mTextureHandle, 0);

        GLES20.glVertexAttribPointer(shader.mTextureHandle, 2, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(shader.mTextureHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(shader.mPositionHandle);
        GLES20.glDisableVertexAttribArray(shader.mNormalHandle);
        GLES20.glDisableVertexAttribArray(shader.mTextureHandle);
    }
}
