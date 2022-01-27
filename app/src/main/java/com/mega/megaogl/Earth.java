package com.mega.megaogl;

public class Earth extends Planet {

    public Earth(Shader shader, int level) {
        super(shader, level, R.drawable.earth1);
        orbit = new Orbit();
        orbit.speed = 0.5f;
        orbit.curAngle = 0;
        orbit.center = 0;
        orbit.radius = 1f;
        orbit.plane = new Utils.vect3(0.1f,1, 0.1f).normalize();
        speed = 1f;
        radius = 0.1f;
        currAngle = 0;
        slope = new Utils.vect3(1f,1f,1f).normalize();
    }
}
