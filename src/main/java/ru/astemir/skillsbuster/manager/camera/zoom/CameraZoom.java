package ru.astemir.skillsbuster.manager.camera.zoom;

import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.client.misc.InterpolationType;

public class CameraZoom {
    private InterpolationType interpolation;
    private EasingType easing;
    private double speed;

    public CameraZoom(double speed, InterpolationType interpolation, EasingType easing) {
        this.speed = speed;
        this.interpolation = interpolation;
        this.easing = easing;
    }

    public double interpolateFov(double from,double to,double ticks,double renderTicks) {
        return interpolation.interpolate(from,to,(easing.ease(ticks)));
    }

    public double getSpeed() {
        return speed;
    }
}
