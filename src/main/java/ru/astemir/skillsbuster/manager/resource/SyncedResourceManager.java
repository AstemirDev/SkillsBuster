package ru.astemir.skillsbuster.manager.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.shader.PostShader;
import ru.astemir.skillsbuster.manager.shader.SBShaderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncedResourceManager extends SBManager.Default<ResourceLocation> {
    private static SyncedResourceManager instance;

    public SyncedResourceManager() {
        instance = this;
    }

    @Override
    public void onLoad(Level level) {
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Void>> tasks = new ArrayList<>();
        try {
            for (ResourceLocation resource : entries()) {
                tasks.add(() -> {
                    AbstractTexture texture = manager.getTexture(resource);
                    if (texture != null) {
                        texture.reset(manager, resourceManager, resource, null);
                    }
                    return null;
                });
            }
        }catch (Throwable e){}
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }


    @Override
    public boolean isClearOnLoad() {
        return false;
    }

    public static ResourceLocation registered(String path){
        ResourceLocation location = new ResourceLocation(path);
        getInstance().add(location);
        return location;
    }

    public static ResourceLocation registered(String id,String path){
        ResourceLocation location = new ResourceLocation(id,path);
        getInstance().add(location);
        return location;
    }

    public static SyncedResourceManager getInstance(){
        return instance;
    }
}
