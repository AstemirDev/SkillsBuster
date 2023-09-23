package ru.astemir.skillsbuster.common.io.json;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import ru.astemir.skillsbuster.manager.gui.SBGui;
import ru.astemir.skillsbuster.manager.model.LayerConfiguration;
import ru.astemir.skillsbuster.manager.model.ModelConfiguration;
import ru.astemir.skillsbuster.manager.gui.nodes.GuiNode;
import ru.astemir.skillsbuster.manager.keybind.SBKeyBind;
import ru.astemir.skillsbuster.client.misc.Transform2D;
import ru.astemir.skillsbuster.manager.actor.ActorConfiguration;
import ru.astemir.skillsbuster.common.utils.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface PropertyHolder {

    JsonDeserializer<PropertyHolder> DESERIALIZER = (json, type, context) -> PropertyHolder.buildHolder((Class<?>) type, json);

    Map<Class<?>, Function<JsonElement,?>> BUILDERS = ImmutableMap.of(
            SBGui.class,(args)->new SBGui(),
            SBKeyBind.class,(args)->new SBKeyBind(),
            ModelConfiguration.class,(args)->new ModelConfiguration(),
            LayerConfiguration.class,(args)->new LayerConfiguration(),
            ActorConfiguration.class,(args)->new ActorConfiguration(),
            GuiNode.class,(args)->GuiNode.fromType(SBJson.getString(args.getAsJsonObject(),"type")),
            Transform2D.class,(args)->new Transform2D()
    );

    default void onLoad(JsonObject jsonObject){};

    default void loadProperties(JsonObject jsonObject){
        for (Field field : ReflectionUtils.getAllFields(getClass())) {
            Class<?> fieldType = field.getType();
            LoadProperty loadProperty = field.getAnnotation(LoadProperty.class);
            if (loadProperty != null) {
                String propertyName = loadProperty.value();
                if (jsonObject.has(propertyName)) {
                    Object value;
                    if (fieldType.isInstance(PropertyHolder.class)) {
                        JsonElement fieldJson = jsonObject.get(propertyName);
                        value = buildHolder(field.getType(),fieldJson);
                    }else
                    if (fieldType.isAssignableFrom(List.class)){
                        JsonArray jsonArray = jsonObject.getAsJsonArray(propertyName);
                        List<Object> list = ReflectionUtils.newList(fieldType);
                        for (JsonElement element : jsonArray) {
                            list.add(deserialize(element,ReflectionUtils.getGeneric(field,0)));
                        }
                        value = list;
                    }else
                    if (fieldType.isAssignableFrom(Map.class)){
                        JsonObject jsonMap = jsonObject.getAsJsonObject(propertyName);
                        Map<Object,Object> map = ReflectionUtils.newMap(fieldType);
                        for (String key : jsonMap.keySet()) {
                            map.put(key,deserialize(jsonMap.get(key),ReflectionUtils.getGeneric(field,1)));
                        }
                        value = map;
                    }else {
                        value = deserialize(jsonObject.get(propertyName), field.getType());
                    }
                    ReflectionUtils.setFieldValue(this, field, value);
                }
            }
        }
        onLoad(jsonObject);
    }

    static Object deserialize(JsonElement json,Class<?> className){
        if (PropertyHolder.class.isAssignableFrom(className)){
            return buildHolder(className,json);
        }
        if (className.isEnum()){
            return SBJson.asEnum(json,(Class<? extends Enum>)className);
        }
        return SBJson.as(json, className);
    }

    static <T extends PropertyHolder> T buildHolder(Class<?> className,JsonElement jsonElement){
        T result = (T) getBuilder(className).apply(jsonElement);
        if (jsonElement.isJsonObject()) {
            result.loadProperties(jsonElement.getAsJsonObject());
        }
        return result;
    }

    static Function<JsonElement,?> getBuilder(Class<?> className){
        for (Class<?> aClass : BUILDERS.keySet()) {
            if (className == aClass || aClass.isAssignableFrom(className)){
                return BUILDERS.get(aClass);
            }
        }
        return null;
    }


}
