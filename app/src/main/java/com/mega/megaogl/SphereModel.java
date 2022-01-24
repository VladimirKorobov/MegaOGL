package com.mega.megaogl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;

public class SphereModel extends Model{

    Texture earth;

    public SphereModel(Shader shader, int level) {
        super(shader);
        earth = new Texture(R.drawable.klipartz);
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
        createBuffers(level);
    }
    private void createBuffers(int level) {
        float[][] vertices = new float[1][];
        short[][] indices = new short[1][];
        //Utils.CalcSphere(vertices, indices, level);
        Utils.CalcSphereForCube(vertices, indices, level);
        //Utils.CalcRect(vertices, indices);

        vertexBuffer = ByteBuffer.allocateDirect(vertices[0].length * Renderer.mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices[0]).position(0);

        indicesBuffer = ByteBuffer.allocateDirect(indices[0].length * Renderer.mBytesPerShort)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices[0]).position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity()
                * Renderer.mBytesPerFloat, vertexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity()
                * Renderer.mBytesPerShort, indicesBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    @Override
    public void draw() {
        int offset = 0;
        int stride = (3 + 3 + 2) * Renderer.mBytesPerFloat;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);
        GLES20.glVertexAttribPointer(shader.mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        offset += 3 * Renderer.mBytesPerFloat;

        GLES20.glVertexAttribPointer(shader.mNormalHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(shader.mNormalHandle);

        // Texture
        offset += 3 * Renderer.mBytesPerFloat;

        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, earth.textureIds[0]);
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
