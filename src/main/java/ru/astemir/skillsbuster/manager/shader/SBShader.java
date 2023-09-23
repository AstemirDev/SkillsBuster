package ru.astemir.skillsbuster.manager.shader;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.render.RenderCall;
import org.astemir.api.lib.shimmer.ShimmerLib;
import ru.astemir.skillsbuster.manager.shader.ShaderRenderFunction;

public enum SBShader {
    FLICKER((model,stack,renderType,packedLight,packedOverlay,r,g,b,a)->renderShader(PostProcessing.FLICKER,model,stack,renderType,packedLight,packedOverlay,r,g,b,a)),
    WARP((model,stack,renderType,packedLight,packedOverlay,r,g,b,a)->renderShader(PostProcessing.WARP,model,stack,renderType,packedLight,packedOverlay,r,g,b,a)),
    HALFTONE((model,stack,renderType,packedLight,packedOverlay,r,g,b,a)->renderShader(PostProcessing.HALFTONE,model,stack,renderType,packedLight,packedOverlay,r,g,b,a)),
    DOT((model,stack,renderType,packedLight,packedOverlay,r,g,b,a)->renderShader(PostProcessing.DOT_SCREEN,model,stack,renderType,packedLight,packedOverlay,r,g,b,a)),
    VHS((model,stack,renderType,packedLight,packedOverlay,r,g,b,a)-> renderShader(PostProcessing.VHS,model,stack,renderType,packedLight,packedOverlay,r,g,b,a)),
    SHIMMER((model,stack,renderType,packedLight,packedOverlay,r,g,b,a)-> renderShader(PostProcessing.BLOOM_UNREAL,model,stack,renderType,ShimmerLib.LIGHT_UNSHADED,packedOverlay,r,g,b,a));
    private ShaderRenderFunction function;
    SBShader(ShaderRenderFunction function) {
        this.function = function;
    }
    public ShaderRenderFunction getFunction() {
        return function;
    }
    public static void renderShader(PostProcessing postProcessing, SkillsModel model, PoseStack stack, RenderType renderType, int packedLight, int packedOverlay, float r, float g, float b, float a){
        PoseStack copyStack = RenderUtils.copyPoseStack(stack);
        postProcessing.postEntityForce((source) -> {
            VertexConsumer consumer = source.getBuffer(renderType);
            model.renderModel(copyStack, consumer, packedLight, packedOverlay, r, g, b, a, RenderCall.LAYER, false);
        });
        ShimmerLib.renderEntityPost();
    }
}
