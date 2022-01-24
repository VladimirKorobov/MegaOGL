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

    static final int mBytesPerFloat = 4;
    static final int mBytesPerShort = 2;

    double angle = 0;
    double dAngle = 0.5;

    Shader shader;
    Model sphere1;
    Model sphere2;
    Model sphere3;


    public Renderer() {
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
// Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.7f, 1.0f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 2.5f;

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
        //setupBuffers();
        sphere1 = new SphereModel(shader, 15);
        //sphere2 = new SphereModel(shader, 2);
        //sphere3 = new SphereModel(shader, 3);

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
        final float far = 1000.0f;

        //Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

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
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        final float[] lightDir = new float[] {0, 0, -1};
        final float[] normalMatrix = new float[9];
        final Utils.vect3 r = new Utils.vect3(0,1,0).normalize();
        final Utils.vect3 r1 = new Utils.vect3(1,0,0).normalize();
        final Utils.vect3 s = new Utils.vect3(0.5f,0.5f,0.5f);
        shader.setLight(lightDir);

        double x = 0.5;
        double z = 0.5;
        double a = Math.atan2(z, x) * 180 / Math.PI;

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0.5f, 0);
        Matrix.rotateM(mModelMatrix, 0, (float)angle, r.x, r.y, r.z);
        Matrix.scaleM(mModelMatrix, 0,  s.x, s.y, s.z);
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Create normal matrix
        getNormalMatrix(mModelMatrix, normalMatrix);
        GLES20.glUniformMatrix3fv(shader.mNormalMatrixHandle, 1, false, normalMatrix, 0);
        sphere1.draw();
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, -0.5f, 0);
        Matrix.rotateM(mModelMatrix, 0, (float)angle, r1.x, r1.y, r1.z);
        Matrix.scaleM(mModelMatrix, 0,  s.x, s.y, s.z);
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Create normal matrix
        getNormalMatrix(mModelMatrix, normalMatrix);
        GLES20.glUniformMatrix3fv(shader.mNormalMatrixHandle, 1, false, normalMatrix, 0);
        sphere1.draw();
/*
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, -20);
        Matrix.rotateM(mModelMatrix, 0, (float)angle, r.x, r.y, r.z);
        Matrix.scaleM(mModelMatrix, 0,  s.x, s.y, s.z);
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Create normal matrix
        getNormalMatrix(mModelMatrix, normalMatrix);
        GLES20.glUniformMatrix3fv(shader.mNormalMatrixHandle, 1, false, normalMatrix, 0);
        sphere2.draw();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, -0.5f, -20);
        Matrix.rotateM(mModelMatrix, 0, (float)angle, r.x, r.y, r.z);
        Matrix.scaleM(mModelMatrix, 0,  s.x, s.y, s.z);
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Create normal matrix
        getNormalMatrix(mModelMatrix, normalMatrix);
        GLES20.glUniformMatrix3fv(shader.mNormalMatrixHandle, 1, false, normalMatrix, 0);
        sphere3.draw();
*/
        angle += dAngle;
        if(angle >= 360)
            angle = angle - 360;
    }
}
