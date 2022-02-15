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
    static Renderer.Buffers buffers = new Renderer.Buffers();

    public static int sphereLevel = 64;
    private int texId;

    public SphereModel(int texId) {
        this.texId = texId;
        if(buffers.vertices[0] == null)
            Utils.CalcSphere(buffers.vertices, buffers.indices, sphereLevel);

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
        Renderer.createBuffers(buffers);
    }

    @Override
    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        super.draw(modelMat, viewMat, projectionMat);
        if(viewMat == null || projectionMat == null)
            return; //
        createBuffers();
        if(texture == null) {
            texture = new Texture(texId);
        }
        int offset = 0;
        int stride = (3 + 3 + 2) * Renderer.mBytesPerFloat;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers.vbo[0]);

        GLES20.glEnableVertexAttribArray(Renderer.shader.mPositionHandle);
        GLES20.glVertexAttribPointer(Renderer.shader.mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        offset += 3 * Renderer.mBytesPerFloat;

        GLES20.glVertexAttribPointer(Renderer.shader.mNormalHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(Renderer.shader.mNormalHandle);

        // Texture
        offset += 3 * Renderer.mBytesPerFloat;

        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, texture.textureIds[0]);
        GLES20.glUniform1i(Renderer.shader.mTextureHandle, 0);

        GLES20.glVertexAttribPointer(Renderer.shader.mTextureHandle, 2, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(Renderer.shader.mTextureHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers.ibo[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, buffers.indicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(Renderer.shader.mPositionHandle);
        GLES20.glDisableVertexAttribArray(Renderer.shader.mNormalHandle);
        GLES20.glDisableVertexAttribArray(Renderer.shader.mTextureHandle);
    }
}
