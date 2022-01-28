package com.mega.megaogl;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class SolarSystem extends Model {
    private List<Model> items = new ArrayList<>();

    public SolarSystem() {
        final Utils.vect3 s = new Utils.vect3(0.5f,0.5f,0.5f);
        Matrix.translateM(matrix, 0, 0, 0f, -20);
        Matrix.scaleM(matrix, 0,  s.x, s.y, s.z);

        items.add(new Earth());
    }
    @Override
    public void update() {
        super.update();
        for(Model m: items) {
            m.update();
        }
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
