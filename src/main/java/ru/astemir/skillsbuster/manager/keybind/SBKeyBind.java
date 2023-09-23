package ru.astemir.skillsbuster.manager.keybind;


import com.mojang.blaze3d.platform.InputConstants;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;

import java.util.ArrayList;
import java.util.List;

public class SBKeyBind implements PropertyHolder {
    @LoadProperty("key")
    private InputConstants.Key key;
    @LoadProperty("click-type")
    private BindClickType clickType = BindClickType.ON_CLICK;
    @LoadProperty("scripts")
    private List<String> scripts = new ArrayList<>();
    @LoadProperty("ignore-gui")
    private boolean ignoreMenus = false;
    private boolean pressed = false;
    private int clickCount = 0;
    private int releaseCount = 0;

    public boolean isActive(){
        return clickType.getPredicate().test(this);
    }

    public boolean isIgnoreMenus() {
        return ignoreMenus;
    }

    public boolean isDown(){
        return pressed;
    }

    public boolean isDoubleClicked(){
        if (clickCount < 2){
            return false;
        }else{
            clickCount-=2;
            return true;
        }
    }

    public boolean isClicked(){
        if (clickCount == 0){
            return false;
        }else{
            clickCount--;
            return true;
        }
    }

    public boolean isReleased(){
        if (releaseCount == 0){
            return false;
        }else{
            releaseCount--;
            return true;
        }
    }

    public void setPressed(boolean pressed) {
        if (pressed){
            clickCount++;
        }else{
            releaseCount++;
        }
        this.pressed = pressed;
    }

    public InputConstants.Key getKey() {
        return key;
    }

    public List<String> getScripts() {
        return scripts;
    }
}
