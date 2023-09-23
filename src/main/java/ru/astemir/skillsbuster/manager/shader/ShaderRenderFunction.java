package ru.astemir.skillsbuster.manager.shader;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import org.astemir.api.client.model.SkillsModel;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;
import ru.astemir.skillsbuster.common.utils.ReflectionUtils;

public interface ShaderRenderFunction{

    ShaderRenderFunction NONE = (model, poseStack, renderType, packedLight, packedOverlay, r, g, b, a) -> {};

    SBJsonDeserializer<ShaderRenderFunction> DESERIALIZER = (json)->{
        String name = json.getAsString();
        SBShader shader = ReflectionUtils.searchEnum(SBShader.class,name);
        if (shader != null){
            return shader.getFunction();
        }else
        if (SBShaderManager.getInstance().containsEntry(name)){
            return (ShaderRenderFunction) (model, poseStack, renderType, packedLight, packedOverlay, r, g, b, a) -> {
                PostProcessing postProcessing = SBShaderManager.getInstance().get(name).getShader();
                SBShader.renderShader(postProcessing,model,poseStack,renderType,packedLight,packedOverlay,r,g,b,a);
            };
        }else{
            return NONE;
        }
    };


    void render(SkillsModel model, PoseStack poseStack, RenderType renderType, int packedLight, int packedOverlay, float r, float g, float b, float a);
}