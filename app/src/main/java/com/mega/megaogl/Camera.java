package com.mega.megaogl;

import android.opengl.Matrix;

public class Camera {
    public final float[] matrix = new float[16];
    public final float[] mViewMatrix = new float[16];
    public Model parent;
    public float dist = 15;

    public Camera() {
        // Position the eye behind the origin.
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

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(matrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }
    public Camera(Utils.vect3 eye, Utils.vect3 look, Utils.vect3 up) {
        Matrix.setLookAtM(matrix, 0,
                eye.x, eye.y, eye.z,
                look.x, look.y, look.z,
                up.x, up.y, up.z);
    }

    public void update() {
        for(int i = 0; i < matrix.length; i ++)
            mViewMatrix[i] = matrix[i];

        if(parent != null) {
            // Move camera toward the model
            final Utils.vect3 v = new Utils.vect3(0, 0, 0);
            v.matrixMult(parent.currModel);
            Matrix.translateM(mViewMatrix, 0, -v.x, -v.y, -v.z - dist);
        }
    }
}
