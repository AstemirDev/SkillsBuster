package ru.astemir.skillsbuster.manager.camera.motion;

import com.google.gson.JsonObject;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.client.misc.InterpolationType;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;
import ru.astemir.skillsbuster.common.io.json.SBJsonSerializer;

public class CameraFrame {

    public static final SBJsonSerializer<CameraFrame> SERIALIZER = (frame)->{
        JsonObject object = new JsonObject();
        object.addProperty("time",frame.getTime());
        object.addProperty("speed",frame.getSpeed());
        if (frame.hasPosition()) {
            object.add("position", SBJson.serialize(frame.getPosition()));
        }
        if (frame.hasRotation()) {
            object.add("rotation", SBJson.serialize(frame.getRotation()));
        }
        if (frame.hasScale()){
            object.add("scale", SBJson.serialize(frame.getScale()));
        }
        if (frame.hasFov()){
            object.addProperty("fov",frame.getFov());
        }
        object.add("interpolation",SBJson.serialize(frame.getInterpolation()));
        object.add("easing",SBJson.serialize(frame.getEasing()));
        return object;
    };

    public static final SBJsonDeserializer<CameraFrame> DESERIALIZER = (json)->{
        if (json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            double time = SBJson.getDouble(jsonObject,"time",0);
            double speed = SBJson.getDouble(jsonObject,"speed",1);
            double fov = SBJson.getDouble(jsonObject,"fov",-1);
            Vector3 position = SBJson.getOr(jsonObject,"position", Vector3.class,null);
            Vector3 rotation = SBJson.getOr(jsonObject,"rotation", Vector3.class,null);
            Vector3 scale = SBJson.getOr(jsonObject,"scale", Vector3.class,null);
            InterpolationType interpolation = SBJson.getEnum(jsonObject,"interpolation", InterpolationType.class, InterpolationType.LINEAR);
            EasingType easing = SBJson.getEnum(jsonObject,"easing", EasingType.class, EasingType.NONE);
            return new CameraFrame(position,rotation,scale,fov,interpolation,easing,speed,time);
        }
        return null;
    };
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;
    private double fov;
    private InterpolationType interpolation;
    private EasingType easing;
    private double speed;
    private double time;

    public CameraFrame(Vector3 position, Vector3 rotation, Vector3 scale, double fov, InterpolationType interpolation, EasingType easing, double speed, double time) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.fov = fov;
        this.interpolation = interpolation;
        this.easing = easing;
        this.speed = speed;
        this.time = time;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getRotation() {
        return rotation;
    }
    public InterpolationType getInterpolation() {
        return interpolation;
    }

    public EasingType getEasing() {
        return easing;
    }

    public double getSpeed() {
        return speed;
    }

    public Vector3 getScale() {
        return scale;
    }

    public double getFov() {
        return fov;
    }

    public double getTime() {
        return time;
    }

    public boolean hasPosition(){
        return position != null;
    }

    public boolean hasRotation(){
        return rotation != null;
    }

    public boolean hasScale() {
        return scale != null;
    }

    public boolean hasFov(){
        return fov != -1;
    }
}
