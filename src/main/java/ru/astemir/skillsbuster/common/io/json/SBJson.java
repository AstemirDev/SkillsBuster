package ru.astemir.skillsbuster.common.io.json;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.Font;
import org.astemir.api.common.animation.AnimationList;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.render.texture.ModelTexture;
import ru.astemir.skillsbuster.common.script.ScriptHolder;
import ru.astemir.skillsbuster.manager.camera.motion.CameraFrame;
import ru.astemir.skillsbuster.manager.camera.motion.CameraMotion;
import ru.astemir.skillsbuster.manager.gui.SBGui;
import ru.astemir.skillsbuster.manager.model.ModelConfiguration;
import ru.astemir.skillsbuster.manager.gui.nodes.GuiNode;
import ru.astemir.skillsbuster.client.misc.Transform2D;
import ru.astemir.skillsbuster.manager.actor.ActorConfiguration;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import ru.astemir.skillsbuster.common.misc.ValueRange;
import ru.astemir.skillsbuster.common.utils.ReflectionUtils;
import ru.astemir.skillsbuster.manager.shader.ShaderRenderFunction;

import java.util.ArrayList;
import java.util.List;

public interface SBJson {

    Gson GSON = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation().
            registerTypeAdapter(SBGui.class,PropertyHolder.DESERIALIZER).
            registerTypeAdapter(GuiNode.class,PropertyHolder.DESERIALIZER).
            registerTypeAdapter(ActorConfiguration.class,PropertyHolder.DESERIALIZER).
            registerTypeAdapter(ModelConfiguration.class,PropertyHolder.DESERIALIZER).
            registerTypeAdapter(Transform2D.class,Transform2D.DESERIALIZER).
            registerTypeAdapter(ValueRange.class,ValueRange.DESERIALIZER).
            registerTypeAdapter(CameraMotion.class, CameraMotion.DESERIALIZER).
            registerTypeAdapter(CameraFrame.class, CameraFrame.DESERIALIZER).
            registerTypeAdapter(CameraFrame.class, CameraFrame.SERIALIZER).
            registerTypeAdapter(Font.class,SBJsonDeserializer.FONT).
            registerTypeAdapter(Vector2.class,SBJsonDeserializer.VECTOR2).
            registerTypeAdapter(Vector2.class,SBJsonSerializer.VECTOR2).
            registerTypeAdapter(Vector3.class,SBJsonDeserializer.VECTOR3).
            registerTypeAdapter(Vector3.class,SBJsonSerializer.VECTOR3).
            registerTypeAdapter(Color.class,SBJsonDeserializer.COLOR).
            registerTypeAdapter(ShaderRenderFunction.class,ShaderRenderFunction.DESERIALIZER).
            registerTypeAdapter(AnimationList.class,SBJsonDeserializer.ANIMATION_LIST).
            registerTypeAdapter(InputConstants.Key.class,SBJsonDeserializer.KEY).
            registerTypeAdapter(SBConfig.class, SBConfig.DESERIALIZER).
            registerTypeAdapter(ScriptHolder.class, ScriptHolder.DESERIALIZER).
            registerTypeAdapter(ModelTexture.class,ModelTexture.DESERIALIZER).
            create();

    static List<String> asListString(JsonArray array){
        List<String> list = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }

    static <T> List<T> asList(JsonArray array,Class<T> className){
        List<T> list = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            list.add(SBJson.as(jsonElement,className));
        }
        return list;
    }

    static <T> List<T> getList(JsonObject object, String key, Class<T> className,List<T> defaultValue){
        if (object.has(key)) {
            return asList(object.getAsJsonArray(key),className);
        }
        return defaultValue;
    }

    static boolean getBoolean(JsonObject object, String key, boolean defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    static <T extends Enum> T getEnum(JsonObject object,String key,Class<T> enumClass,T defaultValue){
        if (object.has(key)) {
            return ReflectionUtils.searchEnum(enumClass,object.get(key).getAsString());
        }
        return defaultValue;
    }




    static String getString(JsonObject object,String key,String defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsString();
        }
        return defaultValue;
    }

    static int getInt(JsonObject object,String key,int defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsInt();
        }
        return defaultValue;
    }

    static double getDouble(JsonObject object,String key,double defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsDouble();
        }
        return defaultValue;
    }

    static float getFloat(JsonObject object,String key,float defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsFloat();
        }
        return defaultValue;
    }


    static boolean getBoolean(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsBoolean();
        }
        throw new RuntimeException(key+" not found");
    }

    static <T extends Enum> T getEnum(JsonObject object,String key,Class<T> enumClass){
        if (object.has(key)) {
            return ReflectionUtils.searchEnum(enumClass,object.get(key).getAsString());
        }
        throw new RuntimeException(key+" not found");
    }


    static String getString(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsString();
        }
        throw new RuntimeException(key+" not found");
    }

    static int getInt(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsInt();
        }
        throw new RuntimeException(key+" not found");
    }

    static double getDouble(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsDouble();
        }
        throw new RuntimeException(key+" not found");
    }

    static float getFloat(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsFloat();
        }
        throw new RuntimeException(key+" not found");
    }

    static <T extends Enum> T asEnum(JsonElement element, Class<T> enumClass){
        return ReflectionUtils.searchEnum(enumClass,element.getAsString());
    }

    static <T> T as(JsonElement object,Class<T> className){
        return GSON.fromJson(object,className);
    }

    static <T> T get(JsonObject object,String key, Class<T> className){
        return GSON.fromJson(object.get(key),className);
    }

    static <T> T getOr(JsonObject object,String key, Class<T> className,T defaultValue){
        if (object.has(key)) {
            return GSON.fromJson(object.get(key), className);
        }else{
            return defaultValue;
        }
    }

    static <T> JsonElement serialize(T object){return GSON.toJsonTree(object);}
    static <T> T deserialize(JsonElement json,Class<T> className){
        return GSON.fromJson(json,className);
    }
    static <T> T deserialize(String json,Class<T> className){
        return GSON.fromJson(json,className);
    }

}
