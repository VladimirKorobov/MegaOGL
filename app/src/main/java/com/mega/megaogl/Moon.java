package com.mega.megaogl;

public class Moon extends Planet{
    public Moon(Planet parent) {
        super(parent, R.drawable.moon);
        orbit = new Orbit();
        orbit.speed = 0.5f;
        orbit.curAngle = 0;
        orbit.center = 0;
        orbit.radius = 8900f;//385000f;
        orbit.plane = new Utils.vect3(0.1f,1, 0.1f).normalize();
        speed = 0.1f;
        radius = 1737;
        currAngle = 0;
        slope = new Utils.vect3(0f,1f,0f).normalize();
    }
}
