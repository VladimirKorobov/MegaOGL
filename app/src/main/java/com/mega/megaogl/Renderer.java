package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {
    private float[] mModelMatrix = new float[16];
    private float[] mModelViewMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mColor = new float[4];

    private final int mBytesPerFloat = 4;
    private final int mBytesPerShort = 2;
    private final int mStrideBytes = 3 * mBytesPerFloat;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mNormalDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;

    private int[] sphereVao = new int[1];
    private FloatBuffer sphereVertexBuffer;
    private ShortBuffer sphereIndicesBuffer;
    final int[] vbo = new int[] {0};
    final int[] nbo = new int[] {0};
    final int[] ibo = new int[] {0};
    int err = 0;
    double angle = 0;
    double dAngle = 1.0;

    Shader shader;

    public Renderer() {
    }

    void setupBuffers() {
        // Create Sphere
        float[][] vertices = new float[1][];
        short[][] indices = new short[1][];
        Utils.CalcSphere(vertices, indices, 3);

        //Utils.CalcRect1(vertices, indices);

        sphereVertexBuffer = ByteBuffer.allocateDirect(vertices[0].length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        sphereVertexBuffer.put(vertices[0]).position(0);

        sphereIndicesBuffer = ByteBuffer.allocateDirect(indices[0].length * mBytesPerShort)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        sphereIndicesBuffer.put(indices[0]).position(0);

        err = GLES20.glGetError();
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glGenBuffers(1, ibo, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, sphereVertexBuffer.capacity()
                * mBytesPerFloat, sphereVertexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereIndicesBuffer.capacity()
                * mBytesPerShort, sphereIndicesBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
// Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.7f, 1.0f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -100.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        //setupShaders();
        shader = new Shader();
        shader.use();
        setupBuffers();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
// Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 100.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        final float[] lightDir = new float[] {0, 0, -1};

        int stride = (3 + 3) * mBytesPerFloat;
        int offset = 0;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);
        GLES20.glVertexAttribPointer(shader.mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);

        offset += 3 * mBytesPerFloat;

        GLES20.glVertexAttribPointer(shader.mNormalHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(shader.mNormalHandle);

        Matrix.translateM(mModelMatrix, 0, 0, 0, -2);
        Matrix.rotateM(mModelMatrix, 0, (float)angle, 1, 0, 0);
        angle += dAngle;
        if(angle >= 360)
            angle = angle - 360;

        Matrix.rotateM(mModelMatrix, 0, (float)90, 0, 0, 1);
        //Matrix.rotateM(mModelMatrix, 0, (float)-90, 0, 0, 1);


        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);

        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.invertM(mModelMatrix, 0, mModelMatrix, 0);
        Matrix.transposeM(mModelMatrix, 0, mModelMatrix, 0);

        GLES20.glUniformMatrix4fv(shader.mModelMatrixHandle, 1, false, mModelViewMatrix, 0);
        shader.setLight(lightDir);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphereIndicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(shader.mPositionHandle);
        GLES20.glDisableVertexAttribArray(shader.mNormalHandle);
    }
}
