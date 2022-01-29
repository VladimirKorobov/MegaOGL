package com.mega.megaogl;

import java.util.ArrayList;

public class Earth extends Planet {

    public Earth() {
        super(R.drawable.earth1);
        orbit = new Orbit();
        orbit.speed = 0.1f;
        orbit.curAngle = 0;
        orbit.center = 0;
        orbit.radius = 10f;
        orbit.plane = new Utils.vect3(0.1f,1, 0.1f).normalize();
        speed = 0.1f;
        radius = 2f;
        currAngle = 0;
        slope = new Utils.vect3(1f,1f,1f).normalize();
        satellites = new ArrayList<>();
        satellites.add(new Moon(this));
    }
}
