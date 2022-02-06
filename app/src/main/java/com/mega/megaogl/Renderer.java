package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.mega.megaogl.shaders.ShaderMain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mOrthoMatrix = new float[16];

    static final int mBytesPerFloat = 4;
    static final int mBytesPerShort = 2;

    double angle = 0;
    double dAngle = 0.5;

    public static ShaderMain shader;
    static SolarSystem solarSystem;
    Camera camera = new Camera();
    public RemoteControl remoteControl = new RemoteControl();

    public static class Buffers {
        public float[][] vertices = new float[1][];
        public short[][] indices = new short[1][];

        public int[] vbo = new int[] {0};
        public int[] ibo = new int[] {0};

        public FloatBuffer vertexBuffer;
        public ShortBuffer indicesBuffer;
    }

    public Renderer() {
        if(solarSystem == null) {
            solarSystem = new SolarSystem();
        }
    }

    public static void createBuffers(Buffers buffers) {
        if(buffers.vbo[0] == 0) {
            GLES20.glGenBuffers(1, buffers.vbo, 0);
            int error = GLES20.glGetError();
            GLES20.glGenBuffers(1, buffers.ibo, 0);
            error = GLES20.glGetError();

            buffers.vertexBuffer = ByteBuffer.allocateDirect(buffers.vertices[0].length * Renderer.mBytesPerFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            error = GLES20.glGetError();
            buffers.vertexBuffer.put(buffers.vertices[0]).position(0);
            error = GLES20.glGetError();

            buffers.indicesBuffer = ByteBuffer.allocateDirect(buffers.indices[0].length * Renderer.mBytesPerShort)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            error = GLES20.glGetError();
            buffers.indicesBuffer.put(buffers.indices[0]).position(0);
            error = GLES20.glGetError();

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers.vbo[0]);
            error = GLES20.glGetError();
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffers.vertexBuffer.capacity()
                    * Renderer.mBytesPerFloat, buffers.vertexBuffer, GLES20.GL_STATIC_DRAW);
            error = GLES20.glGetError();

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers.ibo[0]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers.indicesBuffer.capacity()
                    * Renderer.mBytesPerShort, buffers.indicesBuffer, GLES20.GL_STATIC_DRAW);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
// Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.7f, 1.0f);
        shader = new ShaderMain();
        //setupBuffers();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        shader.use();
        Matrix.setIdentityM(mModelMatrix, 0);

        remoteControl.initialize();

        // Update model matrices
        solarSystem.draw(mModelMatrix, null, null);
        camera.parent = solarSystem.cameraOwner;
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
        final float near = 1f;
        final float far = 100000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.orthoM(mOrthoMatrix, 0, left, right, bottom, top, near, far);
    }

    void drawControl() {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        final float[] lightDir = new float[] {0, 0, -1};
        shader.setLight(lightDir);

        shader.use();
        solarSystem.draw(mModelMatrix, remoteControl.mViewMatrix, mProjectionMatrix);

        remoteControl.drawPanel();
        remoteControl.update();

        //camera.update();
        //solarSystem.draw(mModelMatrix, camera.mViewMatrix, mProjectionMatrix);
    }
}
