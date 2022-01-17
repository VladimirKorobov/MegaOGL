package com.mega.megaogl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

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

    public static void getCoord3D(Utils.vect3 v, Utils.vect2 out) {
        int quater = 0;
        final Utils.vect3 front = new Utils.vect3(0, 0, 1);
        final Utils.vect3 right = new Utils.vect3(1, 0, 0);
        final Utils.vect3 back = new Utils.vect3(0, 0, -1);
        final Utils.vect3 left = new Utils.vect3(-1, 0, 0);
        final Utils.vect3 top = new Utils.vect3(0, 1, 0);
        final Utils.vect3 bottom = new Utils.vect3(0, -1, 0);

        Utils.vect3 vect;

        double sin45 = Math.sqrt(2)/2;
        if(v.y >= sin45) {
            quater = 4; // top
            vect = top;
        }
        if(v.y < -sin45) {
            quater = 5; // bottom
            vect = bottom;
        }
        else {
            double xzAng = Math.atan2(v.z, v.x) * 180 / Math.PI;
            if (xzAng >= 45 && xzAng < 135) {
                quater = 0; // front
                vect = front;
            } else if (xzAng >= -45 && xzAng < 45) {
                quater = 1; // right
                vect = right;
            } else if (xzAng >= -135 && xzAng < -45) {
                quater = 2; // back
                vect = back;
            } else {//if((xzAng <= -135 || xzAng > 45)
                quater = 3; // left
                vect = left;
            }
        }

        float t = 1.0f / Utils.vect3.dot(vect, v);
        vect.mult(t); // this is a cross with the flat
        switch(quater) {
            case 0: // front
                out.x = (1.0f + vect.x + 0.5f) / 4;
                out.y = (1.0f + vect.y + 0.5f) / 3;
                break;
            case 1: // right
                out.x = (2.0f - vect.z + 0.5f) / 4;
                out.y = (1.0f + vect.y + 0.5f) / 3;
                break;
            case 2: // back
                out.x = (3.0f - vect.x + 0.5f) / 4;
                out.y = (1.0f + vect.y + 0.5f) / 3;
                break;
            case 3: // left
                out.x = (0.0f + vect.z + 0.5f) / 4;
                out.y = (1.0f + vect.y + 0.5f) / 3;
                break;
            case 4: // top
                out.x = (1.0f + vect.x + 0.5f) / 4;
                out.y = (0.0f + vect.z + 0.5f) / 3;
                break;
            default: // top
                out.x = (1.0f + vect.x + 0.5f) / 4;
                out.y = (3.0f - vect.z + 0.5f) / 3;
                break;
        }
    }
}
