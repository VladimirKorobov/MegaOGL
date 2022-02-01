package com.mega.megaogl;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class SolarSystem extends Model {
    private List<Model> items = new ArrayList<>();
    public Model cameraOwner;

    public SolarSystem() {
        float f = 1.0f;// / 6371;
        float dist = 6371 + 6371;
        final Utils.vect3 s = new Utils.vect3(f,f,f);
        Matrix.translateM(matrix, 0, 0, 0f, -dist);
        Matrix.scaleM(matrix, 0,  s.x, s.y, s.z);

        cameraOwner = new Earth();
        items.add(cameraOwner);
    }

    @Override
    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        final float[] mat = new float[16];
        Matrix.multiplyMM(mat, 0, modelMat, 0, matrix, 0);

        for(Model m: items) {
            m.draw(mat, viewMat, projectionMat);
        }
    }
}
