package ru.astemir.skillsbuster.manager.gui;

import com.google.gson.JsonObject;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfigManager;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class SBGuiManager extends SBManager.Configurable<SBGui> {

    private static SBGuiManager instance;
    public SBGuiManager() {
        super(ConfigType.GUIS);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        for (SBConfig configuration : configurations) {
            JsonObject guisJson = configuration.getFile().getJsonObject("guis");
            for (String name : guisJson.keySet()) {
                add(name,PropertyHolder.buildHolder(SBGui.class,guisJson.get(name)));
            }
        }
    }

    public static SBGuiManager getInstance() {
        return instance;
    }
}
