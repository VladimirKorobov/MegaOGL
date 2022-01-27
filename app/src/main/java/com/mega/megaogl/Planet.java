package com.mega.megaogl;

import android.opengl.Matrix;

public class Planet extends SphereModel{
    public Orbit orbit;
    public float speed;
    float radius;
    Utils.vect3 slope;
    float currAngle;

    public Planet(Shader shader, int level, int texId) {
        super(shader, level, texId);
    }

    public Planet(Shader shader, int level, int texId,
                  Orbit orbit, float speed, float radius, float currAngle, Utils.vect3 slope) {
        super(shader, level, texId);
        this.orbit = new Orbit(orbit);
        this.slope =  new Utils.vect3(slope);
        this.speed = speed;
        this.radius = radius;
        this.currAngle = currAngle;
    }

    @Override
    public void updateLocation() {
        orbit.update();
        currAngle += speed;
        Matrix.setIdentityM(matrix, 0);
        final Utils.vect3 y = new Utils.vect3(0, 1, 0);

        // Rotate to orbit plane
        Utils.vect3 rotationPlane = Utils.vect3.cross(y, orbit.plane);
        float ang = (float)(Math.asin(rotationPlane.length()) * 180 / Math.PI);
        rotationPlane.normalize();
        Matrix.rotateM(matrix, 0, ang, rotationPlane.x, rotationPlane.y, rotationPlane.z);
        Matrix.rotateM(matrix, 0, orbit.curAngle, y.x, y.y, y.z);
        Matrix.translateM(matrix, 0, orbit.radius, 0, 0);

        // Rotate to planet plane
        rotationPlane = Utils.vect3.cross(y, slope);
        ang = (float)(Math.asin(rotationPlane.length()) * 180 / Math.PI);
        rotationPlane.normalize();
        Matrix.rotateM(matrix, 0, (float)ang, slope.x, slope.y, slope.z);
        Matrix.rotateM(matrix, 0, (float)currAngle, 0, 1, 0);
        Matrix.scaleM(matrix, 0, radius, radius, radius);
    }
}
