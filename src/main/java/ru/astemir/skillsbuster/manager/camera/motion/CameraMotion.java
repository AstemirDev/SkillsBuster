package ru.astemir.skillsbuster.manager.camera.motion;

import com.google.gson.JsonObject;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.client.misc.InterpolationType;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;
import ru.astemir.skillsbuster.manager.NamedEntry;

import java.util.ArrayList;
import java.util.List;

public class CameraMotion implements NamedEntry {

    public static final SBJsonDeserializer<CameraMotion> DESERIALIZER = (json)->{
        if (json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            List<CameraFrame> frames = SBJson.getList(jsonObject,"frames", CameraFrame.class,new ArrayList<>());
            boolean relativePos = SBJson.getBoolean(jsonObject,"relative-pos",true);
            boolean relativeRot = SBJson.getBoolean(jsonObject,"relative-rot",false);
            boolean fixedRot = SBJson.getBoolean(jsonObject,"fixed-rot",true);
            boolean fixedPos = SBJson.getBoolean(jsonObject,"fixed-pos",true);
            return new CameraMotion(frames,relativePos,relativeRot,fixedPos,fixedRot);
        }
        return null;
    };

    private List<CameraFrame> frames;
    private boolean relativePos;
    private boolean relativeRot;
    private boolean fixedPos;
    private boolean fixedRot;

    private String name;

    public CameraMotion(List<CameraFrame> frames, boolean relativePos, boolean relativeRot, boolean fixedPos, boolean fixedRot) {
        this.frames = frames;
        this.relativePos = relativePos;
        this.relativeRot = relativeRot;
        this.fixedPos = fixedPos;
        this.fixedRot = fixedRot;
    }

    public CameraFrame getInterpolatedFrame(double time, double renderTicks) {
        if (frames.isEmpty()) {
            return null;
        }
        if (time <= frames.get(0).getTime()) {
            return frames.get(0);
        }
        if (time >= frames.get(frames.size() - 1).getTime()) {
            return frames.get(frames.size() - 1);
        }
        int startIndex = 0;
        int endIndex = frames.size() - 1;
        while (startIndex < endIndex) {
            int middleIndex = startIndex + (endIndex - startIndex) / 2;
            if (time < frames.get(middleIndex).getTime()) {
                endIndex = middleIndex;
            } else {
                startIndex = middleIndex + 1;
            }
        }
        CameraFrame startFrame = frames.get(startIndex - 1);
        CameraFrame endFrame = frames.get(startIndex);
        double t = (time - startFrame.getTime()) / (endFrame.getTime() - startFrame.getTime());
        return interpolate(startFrame, endFrame, t,renderTicks);
    }

    private CameraFrame interpolate(CameraFrame startFrame, CameraFrame endFrame, double t, double renderTicks) {
        Vector3 position = null;
        if (startFrame.hasPosition() && endFrame.hasPosition()) {
            position = interpolateVector3(startFrame.getPosition(), endFrame.getPosition(), t, endFrame.getInterpolation(), endFrame.getEasing());
        }else
        if (endFrame.hasPosition()) {
            position = endFrame.getPosition();
        }
        Vector3 rotation = null;
        if (startFrame.hasRotation() && endFrame.hasRotation()){
            rotation = interpolateRotVector3(startFrame.getRotation(), endFrame.getRotation(), t,endFrame.getInterpolation(),endFrame.getEasing());
        }else
        if (endFrame.hasRotation()){
            rotation = endFrame.getRotation();
        }
        Vector3 scale = null;
        if (startFrame.hasScale() && endFrame.hasScale()){
            scale = interpolateVector3(startFrame.getScale(), endFrame.getScale(), t,endFrame.getInterpolation(),endFrame.getEasing());
        }else
        if (endFrame.hasScale()){
            scale = endFrame.getScale();
        }
        double fov = -1;
        if (startFrame.hasFov() && endFrame.hasFov()){
            fov = endFrame.getInterpolation().interpolate(startFrame.getFov(),endFrame.getFov(),endFrame.getEasing().ease(t));
        }else
        if (endFrame.hasFov()){
            fov = endFrame.getFov();
        }
        InterpolationType interpolation = startFrame.getInterpolation();
        EasingType easing = startFrame.getEasing();
        double speed = endFrame.getSpeed();
        double time = startFrame.getTime() + t * (endFrame.getTime() - startFrame.getTime());
        double deltaTime = endFrame.getTime() - startFrame.getTime();
        return new CameraFrame(position, rotation, scale,fov,interpolation, easing, speed,((time - startFrame.getTime()) / deltaTime)+renderTicks);
    }


    private Vector3 interpolateVector3(Vector3 startVector, Vector3 endVector, double t, InterpolationType interpolation, EasingType easing) {
        double x = interpolation.interpolate(startVector.getX(), endVector.getX(), easing.ease(t));
        double y = interpolation.interpolate(startVector.getY(), endVector.getY(), easing.ease(t));
        double z = interpolation.interpolate(startVector.getZ(), endVector.getZ(), easing.ease(t));
        return new Vector3(x, y, z);
    }

    private Vector3 interpolateRotVector3(Vector3 startVector, Vector3 endVector, double t, InterpolationType interpolation, EasingType easing) {
        double x = interpolation.interpolateRot(startVector.getX(), endVector.getX(), easing.ease(t));
        double y = interpolation.interpolateRot(startVector.getY(), endVector.getY(), easing.ease(t));
        double z = interpolation.interpolateRot(startVector.getZ(), endVector.getZ(), easing.ease(t));
        return new Vector3(x, y, z);
    }

    public double length(){
        return frames.get(frames.size()-1).getTime();
    }

    public boolean isRelativePos() {
        return relativePos;
    }

    public boolean isRelativeRot() {
        return relativeRot;
    }

    public boolean isFixedPos() {
        return fixedPos;
    }

    public boolean isFixedRot() {
        return fixedRot;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
