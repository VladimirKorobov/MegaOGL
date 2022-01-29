package com.mega.megaogl;

import android.opengl.Matrix;

import java.util.List;

public class Planet extends SphereModel{
    public Orbit orbit;
    public float speed;
    float radius;
    Utils.vect3 slope;
    float currAngle;
    float mass;

    List<Planet> satellites;
    Planet parent;

    public Planet(int texId) {
        super(texId);
    }
    public Planet(Planet parent, int texId) {
        super(texId);
        this.parent = parent;
    }

    public Planet(int texId,
                  Orbit orbit, float speed, float radius, float currAngle, Utils.vect3 slope) {
        super(texId);
        this.orbit = new Orbit(orbit);
        this.slope =  new Utils.vect3(slope);
        this.speed = speed;
        this.radius = radius;
        this.currAngle = currAngle;
    }

    public void updateLocation() {
        orbit.update();
        currAngle += speed;
        final float[] m = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.setIdentityM(m, 0);
        final Utils.vect3 y = new Utils.vect3(0, 1, 0);
        final Utils.vect3 towardPoint = new Utils.vect3();
        towardPoint.init(0, 0, 0);

        // Rotate to orbit plane
        Utils.vect3 rotationPlane = Utils.vect3.cross(y, orbit.plane);
        float ang = (float)(Math.asin(rotationPlane.length()) * 180 / Math.PI);
        rotationPlane.normalize();

        if(parent != null) {
            // Move to the parent
            towardPoint.matrixMult(parent.matrix);
            Matrix.translateM(m, 0, towardPoint.x, towardPoint.y, towardPoint.z);
            towardPoint.init(0, 0, 0);
        }

        Matrix.rotateM(m, 0, ang, rotationPlane.x, rotationPlane.y, rotationPlane.z);
        Matrix.rotateM(m, 0, orbit.curAngle, y.x, y.y, y.z);
        Matrix.translateM(m, 0, orbit.radius, 0, 0);

        towardPoint.matrixMult(m);

        // Rotate to planet plane
        rotationPlane = Utils.vect3.cross(y, slope);
        ang = (float)(Math.asin(rotationPlane.length()) * 180 / Math.PI);
        rotationPlane.normalize();

        // move to orbit trajectory
        Matrix.translateM(matrix, 0, towardPoint.x, towardPoint.y, towardPoint.z);
        Matrix.rotateM(matrix, 0, (float)ang, slope.x, slope.y, slope.z);
        Matrix.rotateM(matrix, 0, (float)currAngle, 0, 1, 0);
        Matrix.scaleM(matrix, 0, radius, radius, radius);
    }

    @Override
    public void draw(float[] modelMat, float[] viewMat, float[] projectionMat) {
        super.draw(modelMat, viewMat, projectionMat);
        if(satellites != null) {
            for(Model s: satellites) {
                s.draw(modelMat, viewMat, projectionMat);
            }
        }
        updateLocation();
    }
}
