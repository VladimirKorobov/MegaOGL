package com.mega.megaogl;

public class Orbit {
    public Orbit() {};
    public Orbit(Orbit orbit) {
        this.radius = orbit.radius;
        this.center = orbit.center;
        this.plane =  new Utils.vect3(orbit.plane);
        this.curAngle = orbit.curAngle;
        this.speed = orbit.speed;
        //this.xyfactor = orbit.xyfactor;
    };

    public void update() {
        curAngle += speed;
    }
    public float radius;
    public float center;
    public float curAngle;
    public float speed;
    //public float xyfactor;
    public Utils.vect3 plane;
}
