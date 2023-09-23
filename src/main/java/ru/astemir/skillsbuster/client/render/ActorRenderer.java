package ru.astemir.skillsbuster.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.client.model.SkillsAnimatedModel;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.render.RenderCall;
import org.astemir.api.client.render.cube.ModelElement;
import org.astemir.api.client.wrapper.SkillsWrapperEntity;
import org.astemir.api.io.ResourceUtils;
import org.astemir.api.math.MathUtils;
import org.astemir.api.math.components.Color;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.manager.model.ModelConfiguration;
import ru.astemir.skillsbuster.manager.model.SBModelManager;
import ru.astemir.skillsbuster.manager.resource.SyncedResourceManager;
import ru.astemir.skillsbuster.manager.actor.EntityActor;
import ru.astemir.skillsbuster.common.entity.EntityTimedState;
import ru.astemir.skillsbuster.manager.actor.ActorConfiguration;

import java.util.function.Function;

public class ActorRenderer extends CustomizedSkillsRenderer<EntityActor, ActorRenderer.ActorWrapper> {
    private ModelConfiguration modelConfig;

    public ActorRenderer(EntityRendererProvider.Context context) {
        super(context, new ActorWrapper());
    }

    @Override
    public void render(EntityActor entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ActorConfiguration configuration = entity.getConfiguration();
        poseStack.pushPose();
        poseStack.scale(configuration.renderScale.x,configuration.renderScale.y,configuration.renderScale.z);
        poseStack.translate(configuration.renderOffset.x,configuration.renderOffset.y,configuration.renderOffset.z);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(configuration.renderRotation.x));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(configuration.renderRotation.y));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(configuration.renderRotation.z));
        this.shadowStrength = configuration.shadowStrength;
        this.shadowRadius = configuration.shadowRadius;
        if (configuration.morphId == null) {
            this.modelConfig = SBModelManager.getModelConfiguration(entity.getConfiguration().model);
            if (modelConfig.light != -1) {
                packedLight = modelConfig.light;
            }
            super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
        }else{
            if (entity.morph == null || !entity.morph.getType().equals(EntityType.byString(configuration.morphId))) {
                CompoundTag tag = new CompoundTag();
                tag.putString("id", configuration.morphId);
                entity.morph = EntityType.loadEntityRecursive(tag, entity.level, Function.identity());
            }
            EntityTimedState state = new EntityTimedState(entity);
            entity.morph.tickCount = entity.tickCount;
            state.apply(entity.morph);
            state.applyClient(entity.morph);
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity.morph);
            renderer.render(entity.morph,yaw,partialTicks,poseStack,bufferSource,packedLight);
        }
        poseStack.popPose();
    }



    @Override
    protected int getSkyLightLevel(EntityActor pEntity, BlockPos pPos) {
        if (modelConfig != null){
            if (modelConfig.blockLight != -1){
                return modelConfig.blockLight;
            }
        }
        return super.getSkyLightLevel(pEntity, pPos);
    }

    @Override
    protected int getBlockLightLevel(EntityActor pEntity, BlockPos pPos) {
        if (modelConfig != null){
            if (modelConfig.blockLight != -1){
                return modelConfig.blockLight;
            }
        }
        return super.getBlockLightLevel(pEntity, pPos);
    }

    public static class ActorWrapper extends SkillsWrapperEntity<EntityActor> {
        private static final SkillsAnimatedModel<EntityActor,IDisplayArgument> defaultModel = createModel(
                ResourceUtils.loadResource(SkillsBuster.MODID,"actors/skills/skills.geo.json"),
                ResourceUtils.loadResource(SkillsBuster.MODID,"actors/skills/skills.animation.json"),
                SyncedResourceManager.registered(SkillsBuster.MODID,"actors/skills/skills.png")
        );

        private ModelConfiguration modelConfig;

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer bufferSource, int packedLight, int packedOverlay, float r, float g, float b, float a) {
            Color color = new Color(r,g,b,a);
            if (getRenderTarget() != null) {
                ActorConfiguration configuration = getRenderTarget().getConfiguration();
                modelConfig = SBModelManager.getModelConfiguration(configuration.model);
                SkillsModel model = getModel(getRenderTarget());
                if (modelConfig != null){
                    color = modelConfig.color;
                    modelConfig.shader.render(getModel(getRenderTarget()), poseStack, modelConfig.actorRenderType.getRenderType(model.getTexture(getRenderTarget())), packedLight, OverlayTexture.NO_OVERLAY, color.r, color.g, color.b, color.a);
                }
            }
            super.renderToBuffer(poseStack, bufferSource, packedLight, packedOverlay, color.r, color.g, color.b, color.a);
        }

        @Override
        public RenderType getDefaultRenderType() {
            if (modelConfig != null) {
                return modelConfig.actorRenderType.getRenderType(getModel(getRenderTarget()).getTexture(getRenderTarget()));
            }else{
                return super.getDefaultRenderType();
            }
        }

        @Override
        public RenderType getRenderType() {
            return super.getRenderType();
        }

        @Override
        public SkillsModel<EntityActor, IDisplayArgument> getModel(EntityActor target) {
            if (modelConfig != null){
                SkillsModel model = SBModelManager.getBakedModel(target.getConfiguration().model);
                if (model != null){
                    return model;
                }
            }
            return defaultModel;
        }
    }

    public static SkillsAnimatedModel<EntityActor,IDisplayArgument> createModel(ResourceLocation model,ResourceLocation animations,ResourceLocation texture){
        return new SkillsAnimatedModel<>(model, animations) {
            private ModelConfiguration configuration;

            @Override
            public void animate(EntityActor animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
                configuration = SBModelManager.getModelConfiguration(animated.getConfiguration().model);
                super.animate(animated, argument, limbSwing, limbSwingAmount, ticks, delta, headYaw, headPitch);
            }

            @Override
            public void customAnimate(EntityActor animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
                boolean use = true;
                if (configuration != null){
                    use = configuration.playerBoneRender;
                }
                if (use) {
                    ModelElement head = getModelElement("bipedHead");
                    ModelElement rightArm = getModelElement("bipedRightArm");
                    ModelElement leftArm = getModelElement("bipedLeftArm");
                    if (head == null) {
                        head = getModelElement("head");
                    }
                    if (head == null) {
                        head = getModelElement("Head");
                    }
                    if (head != null) {
                        head.customRotationY = headYaw * ((float) Math.PI / 180F);
                        head.customRotationX = headPitch * ((float) Math.PI / 180F);
                    }
                    if (rightArm != null) {
                        if (!animated.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                            rightArm.customRotationX = -MathUtils.rad(22.5f);
                        } else {
                            rightArm.customRotationX = 0;
                        }
                    }
                    if (leftArm != null) {
                        if (!animated.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                            leftArm.customRotationX = -MathUtils.rad(22.5f);
                        } else {
                            leftArm.customRotationX = 0;
                        }
                    }
                }
            }

            @Override
            public void onRenderModelCube(ModelElement cube, PoseStack matrixStackIn, VertexConsumer bufferIn, RenderCall renderCall, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
                boolean use = true;
                if (configuration != null){
                    use = configuration.playerBoneRender;
                }
                if (use) {
                    if (renderCall == RenderCall.MODEL) {
                        EntityActor player = getRenderTarget();
                        if (cube.getName().equals("ItemRight")) {
                            if (player != null) {
                                matrixStackIn.pushPose();
                                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                                matrixStackIn.translate(0, 0.0725D, -0.0725D);
                                renderItem(player.getItemBySlot(EquipmentSlot.MAINHAND), ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, matrixStackIn, packedLightIn);
                                matrixStackIn.popPose();
                            }
                        }
                        if (cube.getName().equals("ItemLeft")) {
                            if (player != null) {
                                matrixStackIn.pushPose();
                                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                                matrixStackIn.translate(0, 0.0725D, -0.0725D);
                                renderItem(player.getItemBySlot(EquipmentSlot.OFFHAND), ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, matrixStackIn, packedLightIn);
                                matrixStackIn.popPose();
                            }
                        }
                        bufferIn = returnDefaultBuffer();
                    }
                }
            }

            @Override
            public VertexConsumer returnDefaultBuffer() {
                return super.returnDefaultBuffer();
            }

            @Override
            public ResourceLocation getTexture(EntityActor target) {
                return texture;
            }
        };
    }
}
