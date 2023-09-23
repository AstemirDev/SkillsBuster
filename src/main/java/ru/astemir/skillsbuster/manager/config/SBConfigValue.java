package ru.astemir.skillsbuster.manager.config;

import com.google.gson.JsonObject;
public class SBConfigValue<T> {
    private T value;
    private String name;
    private boolean changed = false;
    private FunctionSync<T> valueChanged;

    public SBConfigValue(String name,JsonObject json,FunctionDefault<T> function,FunctionSync<T> valueChanged) {
        this.name = name;
        this.valueChanged = valueChanged;
        if (json.has(name)) {
            setValue(function.getValue(json,name));
        }
    }

    public SBConfigValue(String name, JsonObject json,Class<T> className, FunctionClassed<T> function,FunctionSync<T> valueChanged) {
        this.name = name;
        this.valueChanged = valueChanged;
        if (json.has(name)) {
            setValue(function.getValue(json,name,className));
        }
    }

    public SBConfigValue(String name,JsonObject json,FunctionDefault<T> function) {
        this.name = name;
        if (json.has(name)) {
            setValue(function.getValue(json,name));
        }
    }

    public SBConfigValue(String name, JsonObject json,Class<T> className, FunctionClassed<T> function) {
        this.name = name;
        if (json.has(name)) {
            setValue(function.getValue(json,name,className));
        }
    }

    public void setValue(T value) {
        if (valueChanged != null) {
            valueChanged.onValueChanged(value);
        }
        this.value = value;
        this.changed = true;
    }

    public void reset(){
        this.changed = false;
    }

    public T getValue() {
        if (isChanged()) {
            return value;
        }
        return null;
    }


    public boolean isChanged() {
        return changed;
    }

    public String getName() {
        return name;
    }

    public interface FunctionSync<T>{
        void onValueChanged(T value);
    }

    public interface FunctionDefault<T>{
        T getValue(JsonObject json,String name);
    }

    public interface FunctionClassed<T>{
        T getValue(JsonObject json,String name,Class<T> valueClass);
    }
}
