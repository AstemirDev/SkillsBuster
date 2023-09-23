package ru.astemir.skillsbuster.manager.gui.style;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextureSet<T extends Enum,K extends Enum> {

    private Map<T, NamedTexture<K>> textureMap = new HashMap<>();

    public TextureSet put(T name,NamedTexture<K> texture){
        this.textureMap.put(name,texture);
        return this;
    }

    public NamedTexture<K> get(T name){
        return textureMap.get(name);
    }

    public Collection<NamedTexture<K>> textures(){
        return textureMap.values();
    }
}
