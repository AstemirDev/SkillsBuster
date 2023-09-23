package ru.astemir.skillsbuster.client.render.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureLoader {

    public class Texture{
        public static AbstractTexture load(ResourceLocation location) {
            return load(location, WrapMode.CLAMP_TO_EDGE, WrapMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR);
        }

        public static AbstractTexture load(ResourceLocation location, WrapMode wrapS, WrapMode wrapT, FilterMode minFilter, FilterMode magFilter) {
            Minecraft minecraft = Minecraft.getInstance();
            AbstractTexture texture = minecraft.getTextureManager().getTexture(location);
            if (texture == null) {
                texture = new SimpleTexture(location);
                minecraft.getTextureManager().register(location, texture);
            }
            if (wrapS != null || wrapT != null) {
                setWrapMode(texture,wrapS, wrapT);
            }
            if (minFilter != null || magFilter != null) {
                setFilterMode(texture,minFilter, magFilter);
            }
            return texture;
        }

        public static AbstractTexture setWrapMode(AbstractTexture texture,WrapMode wrapS,WrapMode wrapT){
            GlStateManager._bindTexture(texture.getId());
            GlStateManager._texParameter(texture.getId(), GL11.GL_TEXTURE_WRAP_S, wrapS.getGlEnum());
            GlStateManager._texParameter(texture.getId(), GL11.GL_TEXTURE_WRAP_T, wrapT.getGlEnum());
            GlStateManager._bindTexture(0);
            return texture;
        }

        public static AbstractTexture setFilterMode(AbstractTexture texture, FilterMode minFilter, FilterMode magFilter) {
            GlStateManager._bindTexture(texture.getId());
            GlStateManager._texParameter(texture.getId(), GL11.GL_TEXTURE_MIN_FILTER, minFilter.getMinFilter());
            GlStateManager._texParameter(texture.getId(), GL11.GL_TEXTURE_MAG_FILTER, magFilter.getMagFilter());
            GlStateManager._bindTexture(0);
            return texture;
        }

        public enum WrapMode {
            CLAMP_TO_EDGE(33071),
            REPEAT(10497),
            MIRRORED_REPEAT(33648);

            private final int glEnum;
            WrapMode(int glEnum) {
                this.glEnum = glEnum;
            }

            public int getGlEnum() {
                return glEnum;
            }
        }

        public enum FilterMode {
            NEAREST(GL11.GL_NEAREST, GL11.GL_NEAREST_MIPMAP_NEAREST),
            LINEAR(GL11.GL_LINEAR, GL11.GL_LINEAR_MIPMAP_LINEAR);

            private final int minFilter;
            private final int magFilter;

            FilterMode(int minFilter, int magFilter) {
                this.minFilter = minFilter;
                this.magFilter = magFilter;
            }

            public int getMinFilter() {
                return minFilter;
            }

            public int getMagFilter() {
                return magFilter;
            }
        }
    }

}
