package ru.astemir.skillsbuster.client.misc;

import com.google.gson.JsonObject;
import org.astemir.api.math.components.Vector2;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;

public class Transform2D {

    public static SBJsonDeserializer<Transform2D> DESERIALIZER = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        Transform2D transform2D = new Transform2D();
        transform2D.setPosition(SBJson.getOr(jsonObject,"position",Vector2.class,new Vector2(0,0)));
        transform2D.setSize(SBJson.getOr(jsonObject,"size",Vector2.class,new Vector2(1,1)));
        transform2D.setScale(SBJson.getOr(jsonObject,"scale",Vector2.class,new Vector2(1,1)));
        transform2D.setRotationDegrees(SBJson.getInt(jsonObject,"rotation",0));
        return transform2D;
    };

    private Vector2 position = new Vector2(0,0);
    private Vector2 size = new Vector2(1,1);
    private Vector2 scale = new Vector2(1,1);
    private int rotationDegrees = 0;

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getSize() {
        return size;
    }

    public Vector2 getScale() {
        return scale;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    public void setScale(Vector2 scale) {
        this.scale = scale;
    }

    public void setRotationDegrees(int rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }
}
