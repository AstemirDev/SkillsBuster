package ru.astemir.skillsbuster.manager.gui.style;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Rect2;
import org.astemir.api.math.components.Vector2;
import ru.astemir.skillsbuster.client.utils.RenderHelper;

import java.util.HashMap;
import java.util.Map;


public class NamedTexture<T extends Enum> {

    private Vector2 atlasSize;
    private ResourceLocation texture;
    private Vector2 uvOffset;
    private Map<T, Rect2> uvs = new HashMap<>();
    public NamedTexture(Vector2 uvOffset, Vector2 atlasSize, ResourceLocation texture) {
        this.uvOffset = uvOffset;
        this.atlasSize = atlasSize;
        this.texture = texture;
    }

    public NamedTexture(Vector2 uvOffset,ResourceLocation texture) {
        this.uvOffset = uvOffset;
        this.atlasSize = new Vector2(256,256);
        this.texture = texture;
    }

    public NamedTexture<T> uv(T name, Rect2 uv){
        this.uvs.put(name,uv);
        return this;
    }

    public Rect2 getUV(T name){
        return uvs.get(name);
    }

    public Vector2 limitSize(T name, Vector2 size, int minHorizontal, int minVertical){
        int h = getHorizontalCount(name,size);
        int v = getVerticalCount(name,size);
        if (h < minHorizontal){
            h = minHorizontal;
        }
        if (v < minVertical){
            v = minVertical;
        }
        Rect2 uv = getUV(name);
        return new Vector2(h*uv.getWidth(),v*uv.getHeight());
    }

    public void render9x9(PoseStack poseStack, T name, Rect2 nodeRect, Vector2 scale,int minHorizontal, int minVertical, Color color){
        Rect2 uv = getUV(name);
        int horizontalBarsCount = getHorizontalCount(name,nodeRect.getSize());
        int verticalBarsCount = getVerticalCount(name,nodeRect.getSize());
        if (horizontalBarsCount < minHorizontal) {
            horizontalBarsCount = minHorizontal;
        }
        if (verticalBarsCount < minVertical) {
            verticalBarsCount = minVertical;
        }
        for (int i = 0; i < horizontalBarsCount; i++) {
            int horizontalSpriteIndex = i == 0 ? 0 : i == horizontalBarsCount - 1 ? 2 : 1;
            for (int j = 0; j < verticalBarsCount; j++) {
                int verticalSpriteIndex = j == 0 ? 0 : j == verticalBarsCount - 1 ? 2 : 1;
                Vector2 pos = new Vector2(nodeRect.getX() + i * uv.getSize().x, nodeRect.getY() + j * uv.getSize().y);
                RenderHelper.blit(poseStack, new Rect2(pos,uv.getSize().mul(scale)),new Rect2(uv.getX()+horizontalSpriteIndex * uvOffset.x, uv.getY() + verticalSpriteIndex *  uvOffset.y, uv.getWidth(), uv.getHeight()),atlasSize, texture,color);
            }
        }
    }

    public int getHorizontalCount(T name,Vector2 size){
        return (int) (size.x/getUV(name).getWidth());
    }

    public int getVerticalCount(T name,Vector2 size){
        return (int) (size.y/getUV(name).getHeight());
    }

    public Vector2 getAtlasSize() {
        return atlasSize;
    }

    public Vector2 getUvOffset() {
        return uvOffset;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
