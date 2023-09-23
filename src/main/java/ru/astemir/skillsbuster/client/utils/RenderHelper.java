package ru.astemir.skillsbuster.client.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Rect2;
import org.astemir.api.math.components.Vector2;

public class RenderHelper {

    public static void blit(PoseStack pPoseStack, Rect2 rectangle, Rect2 uvRect, Vector2 textureSize, ResourceLocation texture, Color color) {
        innerBlit(pPoseStack, (int) rectangle.getX(), (int) (rectangle.getX() + rectangle.getWidth()), (int) rectangle.getY(), (int) (rectangle.getY() + rectangle.getHeight()), 0, (int) uvRect.getWidth(), (int) uvRect.getHeight(), uvRect.getX(), uvRect.getY(), (int) textureSize.getX(), (int) textureSize.getY(),texture,color);
    }
    private static void innerBlit(PoseStack pPoseStack, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pUWidth, float pVHeight, float pUOffset, float pVOffset, float pTextureWidth, float pTextureHeight,ResourceLocation texture, Color color) {
        innerBlit(pPoseStack.last().pose(), pX1, pX2, pY1, pY2, pBlitOffset, (pUOffset + 0.0F) / (float)pTextureWidth, (pUOffset + pUWidth) / pTextureWidth, (pVOffset + 0.0F) / pTextureHeight, (pVOffset + pVHeight) / pTextureHeight,texture,color);
    }
    private static void innerBlit(Matrix4f pMatrix, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV,ResourceLocation texture, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(pMatrix, pX1, pY2, pBlitOffset).uv(pMinU, pMaxV).color(color.r,color.g,color.b,color.a).endVertex();
        bufferbuilder.vertex(pMatrix, pX2, pY2, pBlitOffset).uv(pMaxU, pMaxV).color(color.r,color.g,color.b,color.a).endVertex();
        bufferbuilder.vertex(pMatrix, pX2, pY1, pBlitOffset).uv(pMaxU, pMinV).color(color.r,color.g,color.b,color.a).endVertex();
        bufferbuilder.vertex(pMatrix, pX1, pY1, pBlitOffset).uv(pMinU, pMinV).color(color.r,color.g,color.b,color.a).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
    }

    public static void fillColor(PoseStack pPoseStack, Rect2 rect, Color color) {
        innerFill(pPoseStack.last().pose(), rect.getX(), rect.getY(), rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight(), color);
    }

    private static void innerFill(Matrix4f pMatrix, float pMinX, float pMinY, float pMaxX, float pMaxY, Color color) {
        if (pMinX < pMaxX) {
            float i = pMinX;
            pMinX = pMaxX;
            pMaxX = i;
        }
        if (pMinY < pMaxY) {
            float j = pMinY;
            pMinY = pMaxY;
            pMaxY = j;
        }
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pMatrix, (float)pMinX, (float)pMaxY, 0.0F).color(color.r, color.g, color.b, color.a).endVertex();
        bufferbuilder.vertex(pMatrix, (float)pMaxX, (float)pMaxY, 0.0F).color(color.r, color.g, color.b, color.a).endVertex();
        bufferbuilder.vertex(pMatrix, (float)pMaxX, (float)pMinY, 0.0F).color(color.r, color.g, color.b, color.a).endVertex();
        bufferbuilder.vertex(pMatrix, (float)pMinX, (float)pMinY, 0.0F).color(color.r, color.g, color.b, color.a).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }


    public static void drawString(PoseStack poseStack, Font font, String text, Vector2 position, Vector2 scale, Color color) {
        if (!text.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(position.x*(1-scale.x),position.y*(1-scale.y),0);
            poseStack.scale(scale.x,scale.y,1);
            font.drawShadow(poseStack, text, position.x, position.y, color.getRGBA());
            poseStack.popPose();
        }
    }

    public static void drawStringCentered(PoseStack poseStack, Font font, String text, Vector2 position, Vector2 scale,Color color) {
        if (!text.isEmpty()) {
            float width = font.width(text);
            float height = font.wordWrapHeight(text, (int) width);
            poseStack.pushPose();
            poseStack.translate(position.x*(1-scale.x),position.y*(1-scale.y),0);
            poseStack.scale(scale.x,scale.y,1);
            font.drawShadow(poseStack, text, position.x - width / 2, position.y - height / 2, color.getRGBA());
            poseStack.popPose();
        }
    }
    public static void drawString(PoseStack poseStack, Font font, String text, Vector2 position, Color color) {
        if (!text.isEmpty()) {
            font.draw(poseStack, text, position.x,position.y, color.getRGBA());
        }
    }

    public static void drawStringCentered(PoseStack poseStack, Font font, String text, Vector2 position, Color color) {
        if (!text.isEmpty()) {
            float width = font.width(text);
            float height = font.wordWrapHeight(text, (int) width);
            font.drawShadow(poseStack, text, position.x - width / 2, position.y - height / 2, color.getRGBA());
        }
    }
}
