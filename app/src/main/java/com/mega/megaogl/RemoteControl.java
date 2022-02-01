package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.mega.megaogl.shaders.ShaderCtrl;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;

public class RemoteControl {
    final Utils.vect3 right = new Utils.vect3(1, 0, 0);
    final Utils.vect3 up = new Utils.vect3(0, 1, 0);
    final Utils.vect3 dir = new Utils.vect3(0, 0, 1);
    final Utils.vect3 pos = new Utils.vect3(0, 0, 1);

    final public float[] mViewMatrix = new float[16];
    final private float[] panelOrthoMatrix = new float[16];
    final private float[] panelMatrix = new float[16];

    float speed = 0;
    float speedInc = 0.1f;
    public OGLView view;

    Renderer.Buffers buffers;
    Texture texture;
    ShaderCtrl shader;
    final float buttonSize = 0.2f;

    public float[] buttonCoord;

    public RemoteControl() {
    }

    public void speedInc() {
        speed += speedInc;
    }
    public void speedDec() {
        speed -= speedInc;
    }

    private void createMatrix() {
        final float[] model = new float[16];
        final float ratio = (float) view.width / view.height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 0f;
        final float far = 100000.0f;

        Matrix.orthoM(panelOrthoMatrix, 0, left, right, bottom, top, near, far);

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -100.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(panelMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        Matrix.multiplyMM(panelMatrix, 0, panelOrthoMatrix, 0, panelMatrix, 0);
    }
    private void createButtom() {
        texture = new Texture(R.drawable.button1);
        float[] vert = new float[] {
                -1, 1, 0, 0, 0,
                -1, -1, 0, 0, 1,
                1, -1, 0, 1, 1,
                1, 1, 0, 1, 0
        };
        short[] ind = new short[] {
                0, 1, 2, 2, 3, 0
        };

        buffers = new Renderer.Buffers();
        buffers.vertices[0] = vert;
        buffers.indices[0] =  ind;
        Renderer.createBuffers(buffers);
    }

    public void initialize() {
        shader =  new ShaderCtrl();
        createMatrix();
        createButtom();
    }

    public void sizeChanged(float width, float height) {
        float factor = (float)view.width / view.height;
        final float[] coord = new float[] {
                1 * buttonSize - factor, 2 * buttonSize,
                1 * buttonSize - factor, 3 * buttonSize - 1,
                -2 * buttonSize + factor, 2 * buttonSize,
                -2 * buttonSize + factor, 3 * buttonSize - 1,
                -3 * buttonSize + factor, 0,
                -1 * buttonSize + factor, 0
        };
        buttonCoord = coord;

    }
    public void drawPanel() {
        shader.use();
        drawButtons(buttonCoord);

        //drawButton(buttonSize - factor, buttonSize);
    }
    public void drawButtons(float[] coord) {
        final float[] mat = new float[16];
        int offset = 0;
        int stride = (3 + 2) * Renderer.mBytesPerFloat;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers.vbo[0]);
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);
        GLES20.glVertexAttribPointer(shader.mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);

        offset += 3 * Renderer.mBytesPerFloat;
        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, texture.textureIds[0]);
        GLES20.glUniform1i(shader.mTextureHandle, 0);

        GLES20.glVertexAttribPointer(shader.mTextureHandle, 2, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(shader.mTextureHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers.ibo[0]);

        for(int i = 0; i < coord.length; i += 2) {
            Matrix.setIdentityM(mat, 0);
            Matrix.translateM(mat, 0, coord[i], coord[i + 1], 0);
            Matrix.scaleM(mat, 0, buttonSize, buttonSize, buttonSize);
            Matrix.multiplyMM(mat, 0, panelMatrix, 0, mat, 0);
            GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mat, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, buffers.indicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);

        }
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(shader.mPositionHandle);
        GLES20.glDisableVertexAttribArray(shader.mTextureHandle);
    }
    public void drawButton(float x, float y) {
        final float[] mat = new float[16];
        Matrix.setIdentityM(mat, 0);
        Matrix.translateM(mat, 0, x, y, 0);
        Matrix.scaleM(mat, 0, buttonSize, buttonSize, buttonSize);
        Matrix.multiplyMM(mat, 0, panelMatrix, 0, mat, 0);

        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mat, 0);
        int offset = 0;
        int stride = (3 + 2) * Renderer.mBytesPerFloat;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers.vbo[0]);
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);
        GLES20.glVertexAttribPointer(shader.mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);

        offset += 3 * Renderer.mBytesPerFloat;
        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, texture.textureIds[0]);
        GLES20.glUniform1i(shader.mTextureHandle, 0);

        GLES20.glVertexAttribPointer(shader.mTextureHandle, 2, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(shader.mTextureHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers.ibo[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, buffers.indicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(shader.mPositionHandle);
        GLES20.glDisableVertexAttribArray(shader.mTextureHandle);
    }

    public void speedUpDown(float val) {
        final Utils.vect3 correction = new Utils.vect3();
        correction.init(dir).mult(val).add(up).normalize();
        up.add(correction).normalize();
        // correct dir vector
        dir.init(right).cross(up).normalize();
    }
    public void speedRightLeft(float val) { //left is down means negative
        final Utils.vect3 correction = new Utils.vect3();
        // correct up and right vectors
        correction.init(right).mult(val).add(up).normalize();
        up.add(correction).normalize();
        right.init(up).cross(dir).normalize();

        // correct dir and right vectors
        correction.init(right).mult(-val).add(dir).normalize();
        dir.add(correction).normalize();
        right.init(up).cross(dir).normalize();
    }

    public void update() {
        final Utils.vect3 inc = new Utils.vect3();
        final Utils.vect3 look = new Utils.vect3();
/*
        // correct up/down
        float updown = ((view.leftPos.y + view.rightPos.y) / 2 - view.center.y) * 0.1f;
        speedUpDown(updown);
        // correct right/left
        float rightleft = (view.leftPos.y - view.rightPos.y) * 0.1f;
        speedRightLeft(rightleft);

        // Update position
        inc.init(dir).mult(-speed);
        pos.add(inc);

        look.init(dir).mult(-1);
        Matrix.setLookAtM(mViewMatrix, 0, pos.x, pos.y, pos.z, look.x, look.y, look.z, up.x, up.y, up.z);

 */
    }
    void draw() {


    }
}
