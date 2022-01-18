package com.mega.megaogl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;

public class Texture {
    int[] textureIds = new int[1];
    public Texture(int id) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // R.drawable.klipartz
        final Bitmap bitmap = BitmapFactory.decodeResource(
                MainActivity.appContext.getResources(), id, options);

        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, textureIds[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES20.glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        GLES20.glBindTexture(GL_TEXTURE_2D, textureIds[0]);
    }

    final static Utils.vect3 front = new Utils.vect3(0, 0, 1);
    final static Utils.vect3 right = new Utils.vect3(1, 0, 0);
    final static Utils.vect3 back = new Utils.vect3(0, 0, -1);
    final static Utils.vect3 left = new Utils.vect3(-1, 0, 0);
    final static Utils.vect3 top = new Utils.vect3(0, 1, 0);
    final static Utils.vect3 bottom = new Utils.vect3(0, -1, 0);

    public static void getCoord3D(Utils.vect3 v, Utils.vect2 out, Utils.Face quater) {
        Utils.vect3 vect;
        switch(quater) {
            case front:
                vect = front;
                break;
            case right:
                vect = right;
                break;
            case back:
                vect = back;
                break;
            case left:
                vect = left;
                break;
            case top:
                vect = top;
                break;
            default: // bottom
                vect = bottom;
                break;
        }

        float dot = Utils.vect3.dot(v, vect);
        vect = Utils.vect3.mult(v, 1.0f/dot);
        switch(quater) {
            case front:
                out.x = (1.0f + (vect.x + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case right:
                out.x = (2.0f + (-vect.z + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case back:
                out.x = (3.0f + (-vect.x + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case left:
                out.x = (0.0f + (vect.z + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case top:
                out.x = (1.0f + (vect.x + 1.0f) / 2) / 4;
                out.y = (0.0f + (vect.z + 1.0f) / 2) / 3;
                break;
            default: // bottom
                out.x = (1.0f + (vect.x + 1.0f) / 2) / 4;
                out.y = (2.0f + (-vect.z + 1.0f) / 2) / 3;
                break;
        }
    }

    public static void getCoord3D(Utils.vect3 v, Utils.vect2 out) {
        Utils.Face quater = Utils.Face.front;

        // Find 6 crosses
        float dot = Utils.vect3.dot(v, front);
        float dot_max = 0;
        if(dot > dot_max) {
            dot_max = dot;
            quater = Utils.Face.front;
        }
        dot = Utils.vect3.dot(v, right);
        if(dot > dot_max) {
            dot_max = dot;
            quater = Utils.Face.right;
        }
        dot = Utils.vect3.dot(v, back);
        if(dot > dot_max) {
            dot_max = dot;
            quater = Utils.Face.back;
        }
        dot = Utils.vect3.dot(v, left);
        if(dot > dot_max) {
            dot_max = dot;
            quater = Utils.Face.left;
        }
        dot = Utils.vect3.dot(v, top);
        if(dot > dot_max) {
            dot_max = dot;
            quater = Utils.Face.top;
        }
        dot = Utils.vect3.dot(v, bottom);
        if(dot > dot_max) {
            dot_max = dot;
            quater = Utils.Face.bottom;
        }
        Utils.vect3 vect = Utils.vect3.mult(v, 1.0f/dot_max);
        switch(quater) {
            case front:
                out.x = (1.0f + (vect.x + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case right:
                out.x = (2.0f + (-vect.z + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case back:
                out.x = (3.0f + (-vect.x + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case left:
                out.x = (0.0f + (vect.z + 1.0f) / 2) / 4;
                out.y = (1.0f + (-vect.y + 1.0f) / 2) / 3;
                break;
            case top:
                out.x = (1.0f + (vect.x + 1.0f) / 2) / 4;
                out.y = (0.0f + (vect.z + 1.0f) / 2) / 3;
                break;
            default: // bottom
                out.x = (1.0f + (vect.x + 1.0f) / 2) / 4;
                out.y = (2.0f + (-vect.z + 1.0f) / 2) / 3;
                break;
        }
    }
}
