package ru.astemir.skillsbuster.common.script;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptHolder {

    public static SBJsonDeserializer<ScriptHolder> DESERIALIZER = (json)-> new ScriptHolder(SBJson.as(json,Map.class));

    private Map<String, List<String>> scripts;

    public ScriptHolder(Map<String, List<String>> scripts) {
        this.scripts = scripts;
    }

    public ScriptHolder() {
        this.scripts = new HashMap<>();
    }

    public void runScript(Level level, BlockPos blockPos, String scriptName){
        if (scripts.containsKey(scriptName)) {
            List<String> listScript = scripts.get(scriptName);
            if (level.isClientSide) {
                ScriptNetHandler.sendScriptsToServer(level,null,blockPos,listScript);
            }else{
                ScriptNetHandler.executeScripts(level,null,listScript);
            }
        }
    }
    public void runScript(Level level, Entity entity,String scriptName){
        if (scripts.containsKey(scriptName)) {
            List<String> listScript = scripts.get(scriptName);
            if (level.isClientSide) {
                ScriptNetHandler.sendScriptsToServer(level,entity,entity.blockPosition(),listScript);
            }else{
                ScriptNetHandler.executeScripts(level,entity,listScript);
            }
        }
    }
}
