package ru.astemir.skillsbuster.manager.config;

import com.google.gson.JsonElement;
import net.minecraft.world.level.Level;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.common.io.ModFile;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.utils.SafeUtils;
import ru.astemir.skillsbuster.manager.ManagerDist;
import ru.astemir.skillsbuster.manager.SBManager;

import java.util.ArrayList;
import java.util.List;

public class SBConfigManager extends SBManager.Default<SBConfig>{

    private static SBConfigManager instance;

    public SBConfigManager() {
        instance = this;
    }

    @Override
    public void onLoad(Level level) {
        SBConfig mainConfig = new SBConfig(ConfigType.MAIN,new ModFile(SkillsBuster.MODID +"/config.json",true,false),true);
        for (ManagerDist dist : ManagerDist.values()) {
            for (JsonElement configJson : mainConfig.getFile().get("configurations/"+dist.toString().toLowerCase()).getAsJsonArray()) {
                add(SBJson.as(configJson, SBConfig.class));
            }
        }
    }

    public List<SBConfig> getConfigurationList(ConfigType type){
        List<SBConfig> result = new ArrayList<>();
        for (SBConfig configuration : entries()) {
            if (configuration.isEnabled()) {
                if (configuration.getType() == type) {
                    result.add(configuration);
                }
            }
        }
        return result;
    }

    public static SBConfigManager getInstance(){
        return instance;
    }
}
