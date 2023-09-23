package ru.astemir.skillsbuster.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;

import java.awt.*;

public class Cube3D {

    public static final Vector3[] VERTICES = {
            new Vector3(-1, -1, -1),
            new Vector3(-1, -1,  1),
            new Vector3( 1, -1,  1),
            new Vector3( 1, -1, -1),
            new Vector3(-1,  1, -1),
            new Vector3(-1,  1,  1),
            new Vector3( 1,  1,  1),
            new Vector3( 1,  1, -1)
    };
    public static final Vector2[] TEX_COORDS = {
            new Vector2(0, 0),
            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(1, 0)
    };

    public static final int[][] FACES = {
            {0, 1, 2, 3},
            {7, 6, 5, 4},
            {1, 5, 6, 2},
            {0, 3, 7, 4},
            {3, 2, 6, 7},
            {0, 4, 5, 1}
    };

    public static final Vector3[] NORMALS = {
            new Vector3( 0, -1,  0),
            new Vector3( 0,  1,  0),
            new Vector3(-1,  0,  0),
            new Vector3( 1,  0,  0),
            new Vector3( 0,  0, -1),
            new Vector3( 0,  0,  1)
    };

    private final Vector3 position;
    private final Vector3 size;
    private final Color color;

    public Cube3D(Vector3 position, Vector3 size, Color color) {
        this.position = position;
        this.size = size;
        this.color = color;
    }

    public void render(PoseStack matrixStack, MultiBufferSource multiBufferSource, RenderType renderType){
        VertexFormat vertexFormat = renderType.format();
        matrixStack.pushPose();
        matrixStack.translate(position.x, position.y+size.y, position.z);
        for (int i = 0; i < FACES.length; i++) {
            int[] face = FACES[i];
            Vector3 normal = NORMALS[i / 4];
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            for (int j = 0; j < face.length; j++) {
                Vector3 vertex = Vector3.from(VERTICES[face[j]].toVec3()).mul(size);
                Vector2 texCoord = TEX_COORDS[j];
                if (vertexFormat.hasPosition()) {
                    vertexConsumer = vertexConsumer.vertex(matrixStack.last().pose(), vertex.x, vertex.y, vertex.z);
                }
                if (vertexFormat.hasColor()){
                    vertexConsumer = vertexConsumer.color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
                }
                if (vertexFormat.hasNormal()){
                    vertexConsumer = vertexConsumer.normal(normal.x,normal.y,normal.z);
                }
                if (vertexFormat.hasUV(0)){
                    vertexConsumer = vertexConsumer.uv(texCoord.x,texCoord.y);
                }
                vertexConsumer.endVertex();
            }
        }
        matrixStack.popPose();
    }
}
