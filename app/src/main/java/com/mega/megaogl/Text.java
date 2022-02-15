package com.mega.megaogl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.mega.megaogl.shaders.ShaderText;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;

public class Text {
    private static float nativeTextSize = 64;
    static int textureFontDx;
    static int textureFontDy;
    static int textureCharInRow;
    static int codepageRangeMin = 0x400;
    static int codepageRangeMax = 0x47F;
    static int codepageRange = codepageRangeMax - codepageRangeMin + 1;
    static int textureWidth = 1024;
    static Renderer.Buffers buffers = new Renderer.Buffers();
    static Texture texture;
    static int stride = 2 + 2;
    static ShaderText shader;

    static void generateFontBitmap()
    {
        if(texture != null)
            return;
        Paint paint = new Paint();
        paint.setTextSize(nativeTextSize);

        final float[] widths = new float[1];
        paint.getTextWidths("W", widths);

        textureFontDx = (int)(widths[0] + 0.5f);
        textureFontDy = (int)(nativeTextSize);
        textureCharInRow = codepageRange + 128 - 32;

        int w =  textureFontDx * textureCharInRow;
        int bitmapWidth = 1;
        while(bitmapWidth < w)
            bitmapWidth <<= 1;

        // Create an empty, mutable bitmap
        Bitmap bitmap = Bitmap.createBitmap((int)bitmapWidth, (int)nativeTextSize, Bitmap.Config.ALPHA_8);

        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        // Fill the bitmap with zero alpha
        paint.setAlpha(0);
        canvas.drawPaint(paint);

        // Create vertex buffers
        // For triangle strip: n triangles have 2 * (n + 1) verties
        final float[] vert = new float[(textureCharInRow + 1) * 2 * stride];
        final short[] ind = new short[(textureCharInRow + 1) * 2];
        // Text will be drawn with half-transparent alpha
        paint.setAlpha(128);

        StringBuilder text = new StringBuilder(" ");

        // Factors for texture coordinates
        float texXFactor = 1.f /  ((float)bitmapWidth / textureFontDx);

        int vertexIndex = 0;
        int indexIndex = 0;

        for(int x = 0; x <= textureCharInRow; x ++)
        {
            if(x + 32 > 128)
                text.setCharAt(0, (char)(x + 32 - 128 + codepageRangeMin ));
            else
                text.setCharAt(0, (char)(x + 32));

            String s = text.toString();
            canvas.drawText(s, x * textureFontDx, nativeTextSize, paint);
            paint.getTextWidths(s, widths);

            float texLeft = x * texXFactor;

            ind[indexIndex ++] = (short)(vertexIndex / stride);

            vert[vertexIndex ++] = (float)x * textureFontDx / textureFontDy;
            vert[vertexIndex ++] = -1;
            vert[vertexIndex ++] = texLeft;
            vert[vertexIndex ++] = 1;

            ind[indexIndex ++] = (short)(vertexIndex / stride);

            vert[vertexIndex ++] = (float)x * textureFontDx / textureFontDy;
            vert[vertexIndex ++] = 1;
            vert[vertexIndex ++] = texLeft;
            vert[vertexIndex ++] = 0;
        }

        buffers.vertices[0] = vert;
        buffers.indices[0] = ind;

        Renderer.createBuffers(buffers);
        texture = new Texture(bitmap);
        bitmap.recycle();
        shader = new ShaderText();
    }

    public static void drawText(float[] panelMatrix, String text, float x, float y, float size, float width, float height) {
        if(texture == null)
            return;

        shader.use();

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers.vbo[0]);
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);

        int offset = 0;
        GLES20.glVertexAttribPointer(shader.mPositionHandle, 2, GLES20.GL_FLOAT, false,
                stride * Renderer.mBytesPerFloat, offset);
        // Texture
        offset += 2 * Renderer.mBytesPerFloat;
        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, texture.textureIds[0]);
        GLES20.glUniform1i(shader.mTextureHandle, 0);

        GLES20.glVertexAttribPointer(shader.mTextureHandle, 2, GLES20.GL_FLOAT, false,
                stride * Renderer.mBytesPerFloat, offset);
        GLES20.glEnableVertexAttribArray(shader.mTextureHandle);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers.ibo[0]);

        final float[] mat = new float[16];
        float factor = width / height;
        float scale = size / height;
        x = x * scale - factor;
        y = 1 - y * scale - scale;

        for(int i = 0; i < text.length(); i ++)
        {
            char ch = text.charAt(i);
            int fontIndex;
            if(ch >= codepageRangeMin && ch <= codepageRangeMax) {
                fontIndex = (ch - codepageRangeMin + 128 - 32);
            }
            else {
                if(ch < 32 || ch > 255) {
                    ch = '?';
                }
                fontIndex = (ch - 32);
            }

            Matrix.setIdentityM(mat, 0);
            Matrix.translateM(mat, 0, x, y, 0);
            Matrix.scaleM(mat, 0, scale, scale, scale);
            float off = buffers.vertices[0][(fontIndex * stride) * 2];
            Matrix.translateM(mat, 0, -off + (float)i * textureFontDx / textureFontDy, 0, 0);
            Matrix.multiplyMM(mat, 0, panelMatrix, 0, mat, 0);
            GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, mat, 0);
            offset = (fontIndex) * 2 * 2;
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_SHORT, offset);
        }
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(shader.mPositionHandle);
        GLES20.glDisableVertexAttribArray(shader.mTextureHandle);
    }
}
