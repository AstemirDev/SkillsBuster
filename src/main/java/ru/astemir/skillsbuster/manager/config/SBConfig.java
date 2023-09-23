package ru.astemir.skillsbuster.manager.config;

import com.google.gson.JsonObject;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.common.io.ModFile;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;

public class SBConfig {

    public static SBJsonDeserializer<SBConfig> DESERIALIZER = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        String path = jsonObject.get("path").getAsString();
        boolean preload = jsonObject.get("preload").getAsBoolean();
        boolean enabled = jsonObject.get("enabled").getAsBoolean();
        ModFile file = new ModFile(SkillsBuster.MODID+"/"+path,preload,false);
        return new SBConfig(SBJson.getEnum(jsonObject,"type", ConfigType.class),file,enabled);
    };

    private ConfigType type;
    private ModFile file;
    private boolean enabled;

    public SBConfig(ConfigType type, ModFile file, boolean enabled) {
        this.type = type;
        this.file = file;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ConfigType getType() {
        return type;
    }

    public ModFile getFile() {
        return file;
    }
}
