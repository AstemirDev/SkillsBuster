package ru.astemir.skillsbuster.manager.camera.motion;

import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.astemir.api.client.event.CameraAdvancedSetupEvent;
import org.astemir.api.client.event.CameraPreMatrixResetEvent;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.common.misc.Timeline;
import ru.astemir.skillsbuster.manager.camera.SBCameraManager;

public class CameraMotionController {

    private final Timeline cameraTimeline = new Timeline(0);
    private Vector3 cameraPosition = new Vector3(0,0,0);
    private Vector3 cameraRotation = new Vector3(0,0,0);
    private Vector3 cameraScale = new Vector3(1,1,1);
    private double cameraFov = -1;
    private Vector3 startPos = null;
    private Vector2 startLook = null;
    private CameraMotion currentMotion;
    public void tick(float renderTickTime){
        if (cameraTimeline.isEnabled()){
            CameraFrame frame = currentMotion.getInterpolatedFrame(cameraTimeline.getTicks(),renderTickTime);
            if (frame.getPosition() != null) {
                cameraPosition = frame.getPosition();
            }
            if (frame.getRotation() != null) {
                cameraRotation = frame.getRotation();
            }
            if (frame.getScale() != null){
                cameraScale = frame.getScale();
            }
            if (frame.getFov() != -1){
                cameraFov = frame.getFov();
            }
            cameraTimeline.tick(frame.getSpeed());
        }
    }

    public boolean cameraUpdate(CameraAdvancedSetupEvent e){
        if (cameraTimeline.isEnabled()) {
            if (startPos == null){
                startPos = e.getPosition();
            }
            if (startLook == null){
                startLook = e.getRotation();
            }
            if (currentMotion.isFixedRot()){
                e.setRotation(startLook);
            }
            Vector3 position = currentMotion.isFixedPos() ? startPos : currentMotion.isRelativePos() ? e.getPosition() : new Vector3(0,0,0);
            e.setPosition(position.add(cameraPosition));
            return true;
        }
        return false;
    }

    public boolean cameraUpdateRot(ViewportEvent.ComputeCameraAngles e){
        if (cameraTimeline.isEnabled()) {
            Vector3 rotation =currentMotion.isRelativeRot() ? new Vector3(e.getPitch(), e.getYaw(), e.getRoll()) : new Vector3(0,0,0);
            e.setPitch(rotation.x+cameraRotation.x);
            e.setYaw(rotation.y+cameraRotation.y);
            e.setRoll(rotation.z+cameraRotation.z);
            return true;
        }
        return false;
    }

    public boolean cameraUpdateFov(ViewportEvent.ComputeFov e){
        if (cameraTimeline.isEnabled() && cameraFov != -1) {
            e.setFOV(cameraFov);
            return true;
        }
        return false;
    }

    public boolean cameraUpdateScale(CameraPreMatrixResetEvent e){
        if (cameraTimeline.isEnabled()) {
            e.getPoseStack().scale(cameraScale.x, cameraScale.y, cameraScale.z);
            return true;
        }
        return false;
    }

    public void stopCamera(){
        startPos = null;
        startLook = null;
        cameraPosition = new Vector3(0,0,0);
        cameraRotation = new Vector3(0,0,0);
        cameraScale = new Vector3(1,1,1);
        cameraFov = -1;
        cameraTimeline.stop();
    }

    public void startCamera(String name){
        if (SBCameraManager.getInstance().containsEntry(name)) {
            CameraMotion cameraMotion = SBCameraManager.getInstance().get(name);
            stopCamera();
            currentMotion = cameraMotion;
            cameraTimeline.start(currentMotion.length());
        }
    }
}

