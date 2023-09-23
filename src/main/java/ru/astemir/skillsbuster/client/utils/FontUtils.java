package ru.astemir.skillsbuster.client.utils;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.astemir.api.math.components.Vector2;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FontUtils {

    public static Font createFont(ResourceLocation location,float fontSize,float oversample,float shiftX,float shiftY){
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        FontSet fontSet = new FontSet(Minecraft.getInstance().textureManager,location);
        STBTTFontinfo fontInfo = STBTTFontinfo.malloc();
        try {
            InputStream inputstream = resourceManager.open(location);
            ByteBuffer bytebuffer = TextureUtil.readResource(inputstream);
            bytebuffer.flip();
            if (!STBTruetype.stbtt_InitFont(fontInfo, bytebuffer)) {
                throw new IOException("Invalid ttf");
            }
            fontSet.providers.add(new TrueTypeGlyphProvider(bytebuffer,fontInfo,fontSize,oversample,shiftX,shiftY,""));
            return new Font((a)->fontSet,true);
        } catch (IOException e) {
            return null;
        }
    }

    public static Vector2 fontSize(Font font,String text){
        if (!text.isEmpty()) {
            float width = font.width(text);
            return new Vector2(width,font.wordWrapHeight(text, (int) width));
        }
        return new Vector2(0,font.lineHeight);
    }

    public static Vector2 fontSize(Font font, Component component){
        if (!component.getString().isEmpty()) {
            float width = font.width(component);
            return new Vector2(width,font.wordWrapHeight(component, (int) width));
        }
        return new Vector2(0,font.lineHeight);
    }
}
