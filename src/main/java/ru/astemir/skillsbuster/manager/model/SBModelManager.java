package ru.astemir.skillsbuster.manager.model;


import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.astemir.api.client.model.SkillsAnimatedModel;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.model.SkillsModelLayer;
import org.astemir.api.client.render.RenderCall;
import org.astemir.api.common.misc.ICustomRendered;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.resource.SyncedResourceManager;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import java.util.List;

public class SBModelManager extends SBManager.Configurable<ModelConfiguration> {
    private static SBModelManager instance;

    public SBModelManager() {
        super(ConfigType.MODELS);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        for (SBConfig configuration : configurations) {
            JsonObject modelsJson = configuration.getFile().getJsonObject("models");
            for (String name : modelsJson.keySet()) {
                ModelConfiguration modelConfiguration = PropertyHolder.buildHolder(ModelConfiguration.class,modelsJson.get(name));
                ResourceLocation model = new ResourceLocation(modelConfiguration.model);
                ResourceLocation animation = new ResourceLocation(modelConfiguration.animation);
                if (modelConfiguration.animation != null){
                    modelConfiguration.baked = new SkillsAnimatedModel(resourceManager.getResource(model).isPresent() ? model : null,resourceManager.getResource(animation).isPresent() ? animation : null) {
                        @Override
                        public ResourceLocation getTexture(ICustomRendered target) {
                            return modelConfiguration.texture.getTexture(target);
                        }

                        @Override
                        public void renderModel(PoseStack stack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, RenderCall renderCall, boolean resetBuffer) {
                            if (modelConfiguration.renderSelf) {
                                super.renderModel(stack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha, renderCall, resetBuffer);
                            }
                        }
                    }.interpolation(modelConfiguration.interpolationType).smoothnessType(modelConfiguration.smoothnessType).smoothness(modelConfiguration.smoothness);
                }else{
                    modelConfiguration.baked = new SkillsModel(resourceManager.getResource(model).isPresent() ? model : null) {
                        @Override
                        public ResourceLocation getTexture(ICustomRendered target) {
                            return modelConfiguration.texture.getTexture(target);
                        }

                        @Override
                        public void renderModel(PoseStack stack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, RenderCall renderCall, boolean resetBuffer) {
                            if (modelConfiguration.renderSelf) {
                                super.renderModel(stack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha, renderCall, resetBuffer);
                            }
                        }
                    };
                }
                for (LayerConfiguration layer : modelConfiguration.layers) {
                    modelConfiguration.baked.addLayer(new SkillsModelLayer(modelConfiguration.baked) {
                        @Override
                        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, ICustomRendered instance, int pPackedLight, float pPartialTick, float r, float g, float b, float a) {
                            if (layer.light != -1) {
                                pPackedLight = layer.light;
                            }
                            pPoseStack.pushPose();
                            pPoseStack.scale(layer.size.x,layer.size.y,layer.size.z);
                            pPoseStack.translate(layer.translation.x,layer.translation.y,layer.translation.z);
                            layer.shader.render(modelConfiguration.baked,pPoseStack,layer.actorRenderType.getRenderType(getTexture(instance)),pPackedLight,OverlayTexture.NO_OVERLAY,layer.color.r,layer.color.g,layer.color.b,layer.color.a);
                            if (layer.renderSelf) {
                                VertexConsumer vertexconsumer = pBuffer.getBuffer(layer.actorRenderType.getRenderType(getTexture(instance)));
                                modelConfiguration.baked.renderModel(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, layer.color.r, layer.color.g, layer.color.b, layer.color.a, RenderCall.LAYER,false);
                            }
                            pPoseStack.popPose();
                        }
                        @Override
                        public ResourceLocation getTexture(ICustomRendered instance) {
                            return layer.texture.getTexture(instance);
                        }
                    });
                }
                add(name,modelConfiguration);
            }
        }
    }

    public static ModelConfiguration getModelConfiguration(String name){
        return instance.get(name);
    }
    public static SkillsModel getBakedModel(String name){
        return instance.get(name).baked;
    }

    public static SBModelManager getInstance() {
        return instance;
    }
}
