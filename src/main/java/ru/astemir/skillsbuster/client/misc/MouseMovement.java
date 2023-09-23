package ru.astemir.skillsbuster.client.misc;

public class MouseMovement {

    private float rotX;
    private float rotY;
    private float accumulatedDX;
    private float accumulatedDY;
    private float partialTick;
    public MouseMovement(float rotX, float rotY, float accumulatedDX, float accumulatedDY, float partialTick) {
        this.rotX = rotX;
        this.rotY = rotY;
        this.accumulatedDX = accumulatedDX;
        this.accumulatedDY = accumulatedDY;
        this.partialTick = partialTick;
    }
    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getAccumulatedDX() {
        return accumulatedDX;
    }

    public void setAccumulatedDX(float accumulatedDX) {
        this.accumulatedDX = accumulatedDX;
    }

    public float getAccumulatedDY() {
        return accumulatedDY;
    }

    public void setAccumulatedDY(float accumulatedDY) {
        this.accumulatedDY = accumulatedDY;
    }

    public float getPartialTick() {
        return partialTick;
    }
}
