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

    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mNormalHandle;
    private int mTexCoordHandle;
    private int mModelMatrixHandle;
    private final int mBytesPerFloat = 4;
    private final int mBytesPerShort = 2;
    private final int mStrideBytes = 3 * mBytesPerFloat;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mNormalDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;

    private int[] sphereVao = new int[1];
    private final FloatBuffer sphereVertexBuffer;
    private final ShortBuffer sphereIndicesBuffer;
    final int[] vbo = new int[] {0};
    final int[] nbo = new int[] {0};
    final int[] ibo = new int[] {0};
    int err = 0;
    double angle = 0;
    double dAngle = 1.0;

    public Renderer() {
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
    }

    void setupBuffers() {

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

    void setupShaders() {
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;\n"
                        + "uniform mat4 u_ModelInvTransp;\n"
                        + "attribute vec4 a_Position;\n"
                        + "attribute vec3 a_Normal;\n"
                        + "//attribute vec2 a_TexCoord;\n"
                        + "attribute vec4 a_Color;\n"
                        + "varying vec3 v_Normal;\n"
                        + "//varying vec4 v_TexCoord;\n"
                        + "varying vec4 v_Color;\n"
                        + "varying float light;\n"

                        + "void main()\n"
                        + "{\n"
                        + "   vec3 lightDir = vec3(0.0f, 0.0f, -1.0f);\n"
                        + "   v_Color = a_Color;\n"
                        + "   gl_Position = u_MVPMatrix * a_Position;\n"
//        + "   v_Normal = a_Normal;\n"
                        + "   v_Normal = mat3(u_ModelInvTransp) * a_Normal;\n"
                        + "   light = -dot(v_Normal, lightDir);\n"
                        + "   if (light < 0.0f) light = 0.0f;\n"
                        + "   //v_TexCoord = a_TexCoord; \n"
                        + "}\n";

        final String fragmentShader =
                "varying vec3 v_Normal;\n"
                        +   "   //varying vec4 v_TexCoord;\n"
                        + "//varying vec4 v_Color;\n"
                        + "varying float light;\n"
                        + "void main()\n"
                        + "{\n"
                        + "vec3 col = vec3(1.0f, 1.0f, 1.0f);\n"
                        + "gl_FragColor = vec4(light * col, 1.0f);\n"
                        + "}\n";

        // Load in the vertex shader.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0)
        {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        //mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
        //mTexCoordHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoord");
        mModelMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_ModelInvTransp");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
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

        setupShaders();
        setupBuffers();

        // Enable depth buffer
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Enable back face culling
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

        int stride = (3 + 3) * mBytesPerFloat;
        int offset = 0;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);

        offset += 3 * mBytesPerFloat;

        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, nbo[0]);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false,
                stride, offset);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        Matrix.translateM(mModelMatrix, 0, 0, 0, -2);
        Matrix.rotateM(mModelMatrix, 0, (float)angle, 1, 0, 0);
        angle += dAngle;
        if(angle >= 360)
            angle = angle - 360;

        Matrix.rotateM(mModelMatrix, 0, (float)90, 0, 0, 1);
        //Matrix.rotateM(mModelMatrix, 0, (float)-90, 0, 0, 1);


        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.invertM(mModelMatrix, 0, mModelMatrix, 0);
        Matrix.transposeM(mModelMatrix, 0, mModelMatrix, 0);

        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelViewMatrix, 0);

        mColor[0] = 1.0f;
        mColor[1] = 1.0f;
        mColor[2] = 1.0f;
        mColor[3] = 1.0f;
        //GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphereIndicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
    }
}
