package ru.astemir.skillsbuster.manager.shader;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import ru.astemir.skillsbuster.common.io.FileUtils;
import ru.astemir.skillsbuster.manager.NamedEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class PostShader implements NamedEntry {
    private String name;
    private PostProcessing shader;
    private Map<String, IntSupplier> samplers = new HashMap<>();
    private Map<String, Consumer<AbstractUniform>> uniforms = new HashMap<>();
    private Consumer<Float> onUpdate;

    public PostShader(String name,ResourceLocation path){
        this.name = name;
        this.shader = PostProcessing.registerPost(name,path);
    }

    public PostShader samplerUnsafe(String name, ResourceLocation location,boolean repeat){
        AbstractTexture texture = Minecraft.getInstance().textureManager.getTexture(location);
        if (repeat){
            texture.bind();
            GlStateManager._texParameter(3553, 10242, 10497);
            GlStateManager._texParameter(3553, 10243,  10497);
        }
        samplers.put(name,texture::getId);
        return this;
    }

    public PostShader sampler(String name, ResourceLocation location,boolean repeat){
        if (!samplers.containsKey(name)) {
            AbstractTexture texture = Minecraft.getInstance().textureManager.getTexture(location);
            if (repeat){
                texture.bind();
                GlStateManager._texParameter(3553, 10242, 10497);
                GlStateManager._texParameter(3553, 10243,  10497);
            }
            return sampler(name, texture::getId);
        }
        return this;
    }

    public PostShader sampler(String name, IntSupplier textureId){
        if (!samplers.containsKey(name)) {
            this.samplers.put(name, textureId);
        }
        return this;
    }

    public PostShader uniform(String name,Consumer<AbstractUniform> uniform){
        this.uniforms.put(name,uniform);
        return this;
    }

    public PostShader updateCallback(Consumer<Float> onUpdate){
        this.onUpdate = onUpdate;
        return this;
    }

    public Consumer<Float> getUpdateCallback() {
        return onUpdate;
    }

    public Map<String, IntSupplier> getSamplers() {
        return samplers;
    }

    public Map<String, Consumer<AbstractUniform>> getUniforms() {
        return uniforms;
    }

    public PostProcessing getShader() {
        return shader;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}