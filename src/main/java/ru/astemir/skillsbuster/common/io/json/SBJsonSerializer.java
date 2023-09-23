package ru.astemir.skillsbuster.common.io.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;

import java.lang.reflect.Type;

public interface SBJsonSerializer<T> extends JsonSerializer<T> {

    SBJsonSerializer<Vector2> VECTOR2 = (object)->{
        JsonArray array = new JsonArray();
        array.add(object.x);
        array.add(object.y);
        return array;
    };

    SBJsonSerializer<Vector3> VECTOR3 = (object)->{
        JsonArray array = new JsonArray();
        array.add(object.x);
        array.add(object.y);
        array.add(object.z);
        return array;
    };

    JsonElement serialize(T object);

    @Override
    default JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return serialize(src);
    }
}
