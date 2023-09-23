package ru.astemir.skillsbuster.mixin.client;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.astemir.skillsbuster.client.misc.MouseMovement;
import ru.astemir.skillsbuster.manager.camera.SBCameraManager;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {

    @Shadow public abstract boolean isMouseGrabbed();
    @Shadow private double lastMouseEventTime;
    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private SmoothDouble smoothTurnX;
    @Shadow @Final private SmoothDouble smoothTurnY;

    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = true)
    public void turnPlayer() {
        double d0 = Blaze3D.getTime();
        double d1 = d0 - this.lastMouseEventTime;
        this.lastMouseEventTime = d0;
        if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            double d4 = this.minecraft.options.sensitivity().get() * (double) 0.6F + (double) 0.2F;
            double d5 = d4 * d4 * d4;
            double d6 = d5 * 8.0D;
            double d2;
            double d3;
            if (this.minecraft.options.smoothCamera) {
             double d7 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d6, d1 * d6);
             double d8 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d6, d1 * d6);
             d2 = d7;
             d3 = d8;
            } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
             this.smoothTurnX.reset();
             this.smoothTurnY.reset();
             d2 = this.accumulatedDX * d5;
             d3 = this.accumulatedDY * d5;
            } else {
             this.smoothTurnX.reset();
             this.smoothTurnY.reset();
             d2 = this.accumulatedDX * d6;
             d3 = this.accumulatedDY * d6;
            }
            MouseMovement mouseMovement = new MouseMovement((float)d2,(float)d3,(float)accumulatedDX,(float)accumulatedDY, minecraft.getPartialTick());
            SBCameraManager.cameraTurn(mouseMovement);
            d2 = mouseMovement.getRotX();
            d3 = mouseMovement.getRotY();
            this.accumulatedDX = mouseMovement.getAccumulatedDX();
            this.accumulatedDY = mouseMovement.getAccumulatedDY();
            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;
            int i = 1;
            if (this.minecraft.options.invertYMouse().get()) {
             i = -1;
            }

            this.minecraft.getTutorial().onMouse(d2, d3);
            if (this.minecraft.player != null) {
             this.minecraft.player.turn(d2, d3 * (double) i);
            }

        } else {
            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;
        }
    }
}
