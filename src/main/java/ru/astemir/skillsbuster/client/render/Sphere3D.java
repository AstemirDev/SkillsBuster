package ru.astemir.skillsbuster.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.astemir.api.math.components.Vector3;

import java.awt.*;

public class Sphere3D {

    private final Vector3 position;
    private final float radius;
    private final Color color;
    private final int numSegments;

    public Sphere3D(Vector3 position, float radius, Color color, int numSegments) {
        this.position = position;
        this.radius = radius;
        this.color = color;
        this.numSegments = numSegments;
    }

    public void render(PoseStack matrixStack, MultiBufferSource multiBufferSource, RenderType renderType) {
        VertexFormat vertexFormat = renderType.format();
        matrixStack.pushPose();
        matrixStack.translate(position.getX(), position.getY()+radius, position.getZ());
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
        for (int j = 0; j < numSegments; ++j) {
            float v1 = (float) j / (float) numSegments;
            float v2 = (float) (j + 1) / (float) numSegments;
            float theta1 = v1 * (float) Math.PI;
            float theta2 = v2 * (float) Math.PI;
            float sinTheta1 = (float) Math.sin(theta1);
            float sinTheta2 = (float) Math.sin(theta2);
            float cosTheta1 = (float) Math.cos(theta1);
            float cosTheta2 = (float) Math.cos(theta2);
            for (int i = 0; i <= numSegments; ++i) {
                float u = (float) i / (float) numSegments;
                float phi = u * 2.0f * (float) Math.PI;
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);
                float x1 = cosPhi * sinTheta1;
                float y1 = cosTheta1;
                float z1 = sinPhi * sinTheta1;
                Vector3 normal1 = new Vector3(x1, y1, z1);
                Vector3 vertex1 = Vector3.from(normal1.toVec3()).mul(radius);
                vertexConsumer = vertexConsumer.vertex(matrixStack.last().pose(), vertex1.getX(), vertex1.getY(), vertex1.getZ());
                if (vertexFormat.hasColor()){
                    vertexConsumer = vertexConsumer.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                }
                if (vertexFormat.hasNormal()){
                    vertexConsumer = vertexConsumer.normal(normal1.getX(), normal1.getY(), normal1.getZ());
                }
                if (vertexFormat.hasUV(0)){
                    vertexConsumer = vertexConsumer.uv(u, v1);
                }
                vertexConsumer.endVertex();

                float x2 = cosPhi * sinTheta2;
                float y2 = cosTheta2;
                float z2 = sinPhi * sinTheta2;
                Vector3 normal2 = new Vector3(x2, y2, z2);
                Vector3 vertex2 = Vector3.from(normal2.toVec3()).mul(radius);
                vertexConsumer = vertexConsumer.vertex(matrixStack.last().pose(), vertex2.getX(), vertex2.getY(), vertex2.getZ());
                if (vertexFormat.hasColor()){
                    vertexConsumer = vertexConsumer.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                }
                if (vertexFormat.hasNormal()){
                    vertexConsumer = vertexConsumer.normal(normal2.getX(), normal2.getY(), normal2.getZ());
                }
                if (vertexFormat.hasUV(0)){
                    vertexConsumer = vertexConsumer.uv(u, v2);
                }
                vertexConsumer.endVertex();
            }
        }
        matrixStack.popPose();
    }
}