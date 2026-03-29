package com.pulse.event.events;

import com.pulse.event.Event;

public class EventMotion extends Event {

    public enum Stage { PRE, POST }

    private final Stage stage;
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    public EventMotion(Stage stage, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.stage = stage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }
    public Stage getStage() { return stage; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public boolean isOnGround() { return onGround; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
}
