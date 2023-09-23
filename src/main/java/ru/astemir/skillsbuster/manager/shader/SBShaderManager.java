package ru.astemir.skillsbuster.manager.shader;


import com.google.gson.JsonObject;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import java.util.List;

public class SBShaderManager extends SBManager.Configurable<PostShader> {

    private static SBShaderManager instance;

    public SBShaderManager() {
        super(ConfigType.SHADER);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        for (PostShader entry : entries()) {
            entry.getShader().onResourceManagerReload(resourceManager);
        }
        clearEntries();
        for (SBConfig configuration : configurations) {
            JsonObject shadersJson = configuration.getFile().getJsonObject("shaders");
            for (String key : shadersJson.keySet()) {
                JsonObject shaderJson = shadersJson.getAsJsonObject(key);
                String path = shaderJson.get("path").getAsString();
                add(key,new PostShader(key,new ResourceLocation(path)));
            }
        }
    }

    @Override
    public boolean isClearOnLoad() {
        return false;
    }

    public static PostShader matchPostShader(String name){
        for (PostShader registeredShader : getInstance().entries()) {
            if (name.contains(registeredShader.getName())){
                return registeredShader;
            }
        }
        return null;
    }

    public static SBShaderManager getInstance() {
        return instance;
    }
}
