package ru.astemir.skillsbuster.mixin.client;

import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import org.astemir.api.mixin.client.MixinGameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.skillsbuster.manager.shader.PostShader;
import ru.astemir.skillsbuster.manager.shader.SBShaderManager;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@Mixin(PostPass.class)
public abstract class MixinExtendPostPass {
    @Shadow
    @Final
    private EffectInstance effect;

    @Inject(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EffectInstance;safeGetUniform(Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/AbstractUniform;", ordinal = 1),remap = true)
    private void extendPasses(float pPartialTicks, CallbackInfo ci) {
        PostShader postShader = SBShaderManager.matchPostShader(effect.getName());
        if (postShader != null){
            if (postShader.getUpdateCallback() != null){
                postShader.getUpdateCallback().accept(pPartialTicks);
            }
            for (Map.Entry<String, Consumer<AbstractUniform>> entry : postShader.getUniforms().entrySet()) {
                entry.getValue().accept(effect.safeGetUniform(entry.getKey()));
            }
        }
    }

    @Inject(method = "process", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/EffectInstance;setSampler(Ljava/lang/String;Ljava/util/function/IntSupplier;)V"),remap = false)
    private void extendSamplers(float pPartialTicks, CallbackInfo ci) {
        PostShader postShader = SBShaderManager.matchPostShader(effect.getName());
        if (postShader != null) {
            for (Map.Entry<String, IntSupplier> entry : postShader.getSamplers().entrySet()) {
                effect.setSampler(entry.getKey(), entry.getValue());
            }
        }
    }
}