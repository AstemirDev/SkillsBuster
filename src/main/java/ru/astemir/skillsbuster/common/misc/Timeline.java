package ru.astemir.skillsbuster.common.misc;

public class Timeline {
    private double length;
    private double ticks = 0;
    private boolean enabled = false;
    private boolean resetOnEnd = true;
    public Timeline(float length) {
        this.length = length;
    }

    public void tick(double speed){
        if (enabled) {
            if (ticks <= length) {
                ticks += speed;
            } else {
                if (resetOnEnd) {
                    ticks = 0;
                    enabled = false;
                }
            }
        }
    }

    public void start(double length){
        this.length = length;
        start();
    }

    public void start(){
        enabled = true;
        ticks = 0;
    }

    public void stop(){
        enabled = false;
        ticks = 0;
    }

    public Timeline pauseAtEnd(){
        resetOnEnd = false;
        return this;
    }

    public double getLength() {
        return length;
    }

    public double getTicks() {
        return ticks;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
