package ru.astemir.skillsbuster.manager.camera.zoom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import org.astemir.api.lib.shimmer.ShimmerLib;
import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.client.misc.InterpolationType;
import ru.astemir.skillsbuster.common.misc.Timeline;
import ru.astemir.skillsbuster.manager.camera.SBCameraManager;

public class ZoomController {

    private final Timeline zoomTimeline = new Timeline(0).pauseAtEnd();
    private double lerpedZoom = -1;
    private double zoomValueFrom = -1;
    private double zoomValueTo = 80;
    public double zoomSpeed = 0.1f;
    public boolean smoothCameraOnZoom = false;
    private CameraZoom zoom;

    public void tick(float renderTickTime){
        if (zoomTimeline.isEnabled()){
            double f1 = (zoomTimeline.getTicks()/ zoomTimeline.getLength());
            lerpedZoom = zoom.interpolateFov(zoomValueFrom, zoomValueTo,f1,renderTickTime);
            zoomTimeline.tick(zoom.getSpeed());
        }
    }

    public boolean fovUpdate(ViewportEvent.ComputeFov e){
        if (zoomTimeline.isEnabled()){
            if (lerpedZoom != -1){
                e.setFOV(lerpedZoom);
            }
            return true;
        }
        return false;
    }

    public void zoomStart(double value, double time, double speed, InterpolationType interpolation, EasingType easing){
        if (!zoomTimeline.isEnabled()) {
            zoom = new CameraZoom(speed, interpolation, easing);
            lerpedZoom = SBCameraManager.getInstance().getFov();
            zoomValueFrom = lerpedZoom;
            zoomValueTo = value;
            zoomTimeline.stop();
            zoomTimeline.start(time);
            if (smoothCameraOnZoom) {
                Minecraft.getInstance().options.smoothCamera = true;
            }
        }
    }

    public void zoomEnd(){
        lerpedZoom = -1;
        zoom = null;
        zoomTimeline.stop();
        if (smoothCameraOnZoom) {
            Minecraft.getInstance().options.smoothCamera = false;
        }
    }

    public void setSmoothCameraOnZoom(boolean smoothCameraOnZoom) {
        this.smoothCameraOnZoom = smoothCameraOnZoom;
    }

    public void setZoomSpeed(double zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }

    public boolean isZooming(){
        return zoomTimeline.isEnabled();
    }

    public double getZoomSpeed() {
        return zoomSpeed;
    }
}
