package ru.astemir.skillsbuster.manager.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.SkillsRenderTypes;

import java.util.function.Function;

public enum ModelRenderType {
    DEFAULT((texture)->RenderType.entityCutoutNoCull(texture)),
    CULL((texture)->RenderType.entitySolid(texture)),
    SMOOTH((texture)->RenderType.entitySmoothCutout(texture)),
    DECAL((texture)->RenderType.entityDecal(texture)),
    GLINT((texture)->RenderType.entityGlint()),
    ALPHA((texture)->RenderType.dragonExplosionAlpha(texture)),
    TRANSPARENT((texture)->RenderType.entityTranslucent(texture)),
    GLOW((texture)-> SkillsRenderTypes.entityTranslucentEmissive(texture)),
    EYES((texture)->RenderType.eyes(texture)),
    EYES_TRANSPARENT((texture)-> SkillsRenderTypes.eyesTransparent(texture)),
    EYES_TRANSPARENT_NO_CULL((texture)-> SkillsRenderTypes.eyesTransparentNoCull(texture));

    private Function<ResourceLocation,RenderType> function;
    ModelRenderType(Function<ResourceLocation, RenderType> function) {
        this.function = function;
    }

    public RenderType getRenderType(ResourceLocation texture){
        return function.apply(texture);
    }
}
