package ru.astemir.skillsbuster.common.io.json;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.common.animation.Animation;
import org.astemir.api.common.animation.AnimationList;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.utils.FontUtils;

import java.lang.reflect.Type;

public interface SBJsonDeserializer<T> extends JsonDeserializer<T> {
    SBJsonDeserializer<Font> FONT = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            String path = jsonObject.get("path").getAsString();
            float fontSize = SBJson.getFloat(jsonObject,"size",11);
            float oversample = SBJson.getFloat(jsonObject,"oversample",1);
            Vector2 shift = SBJson.getOr(jsonObject,"shift",Vector2.class,new Vector2(0,0));
            Font font = FontUtils.createFont(new ResourceLocation(path),fontSize,oversample,shift.x,shift.y);
            return font;
        }else
        if (json.isJsonPrimitive()){
            String path = json.getAsString();
            return FontUtils.createFont(new ResourceLocation(path),11,1,0,0);
        }
        return Minecraft.getInstance().font;
    };

    SBJsonDeserializer<AnimationList> ANIMATION_LIST = (json)->{
        AnimationList animationList = new AnimationList();
        if (json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            for (String animationName : jsonObject.keySet()) {
                JsonObject animationJson = jsonObject.getAsJsonObject(animationName);
                Animation animation = new Animation(animationName,SBJson.getFloat(animationJson,"length",1.0f));
                animation.layer(SBJson.getInt(animationJson,"layer",0));
                animation.speed(SBJson.getFloat(animationJson,"speed",1));
                animation.smoothness(SBJson.getFloat(animationJson,"smoothness",1));
                animation.priority(SBJson.getInt(animationJson,"priority",0));
                animation.loop(SBJson.getEnum(animationJson,"loop", Animation.Loop.class, Animation.Loop.TRUE));
                animationList.addAnimation(animation);
            }
        }
        return animationList;
    };

    SBJsonDeserializer<Color> COLOR = (json)->{
        if (json.isJsonPrimitive()){
            String colorStr = json.getAsString();
            if (colorStr.startsWith("#")) {
                return Color.fromHexString(colorStr);
            }else{
                return Color.fromName(colorStr);
            }
        }else
        if (json.isJsonArray()){
            JsonArray array = json.getAsJsonArray();
            float r = array.get(0).getAsFloat()/255f;
            float g = array.get(1).getAsFloat()/255f;
            float b = array.get(2).getAsFloat()/255f;
            float a = 1;
            if (array.size() > 3){
                a = array.get(3).getAsFloat()/255f;
            }
            return new Color(r,g,b,a);
        }
        return Color.BLACK;
    };

    SBJsonDeserializer<Vector3> VECTOR3 = (json)->{
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            return new Vector3(array.get(0).getAsDouble(), array.get(1).getAsDouble(),array.get(2).getAsDouble());
        }else
        if (json.isJsonPrimitive()){
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isString()){
                String[] args = primitive.getAsString().split(",");
                return new Vector3(Double.parseDouble(args[0]),Double.parseDouble(args[1]),Double.parseDouble(args[2]));
            }else
            if (primitive.isNumber()){
                float f = primitive.getAsNumber().floatValue();
                return new Vector3(f,f,f);
            }
        }
        return new Vector3(0,0,0);
    };

    SBJsonDeserializer<Vector2> VECTOR2 = (json)->{
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            return new Vector2(array.get(0).getAsDouble(), array.get(1).getAsDouble());
        }else
        if (json.isJsonPrimitive()){
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isString()){
                String[] args = primitive.getAsString().split(",");
                return new Vector2(Double.parseDouble(args[0]),Double.parseDouble(args[1]));
            }else
            if (primitive.isNumber()){
                float f = primitive.getAsNumber().floatValue();
                return new Vector2(f,f);
            }
        }
        return new Vector2(0,0);
    };

    SBJsonDeserializer<InputConstants.Key> KEY = (json)-> InputConstants.getKey(json.getAsString());

    T deserialize(JsonElement json);

    @Override
    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json);
    }
}
