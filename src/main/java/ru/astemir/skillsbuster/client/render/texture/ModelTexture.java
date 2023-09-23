package ru.astemir.skillsbuster.client.render.texture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.common.io.json.SBJsonDeserializer;
import ru.astemir.skillsbuster.manager.actor.EntityActor;
import ru.astemir.skillsbuster.manager.resource.SyncedResourceManager;

import java.util.Arrays;

public sealed interface ModelTexture<T> {

    SBJsonDeserializer<ModelTexture> DESERIALIZER = (json)->{
        if (json.isJsonPrimitive()){
            return new Default(json.getAsString());
        }else
        if (json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray array = jsonObject.getAsJsonArray("paths");
            ResourceLocation[] locations = new ResourceLocation[array.size()];
            for (int i = 0; i < locations.length; i++) {
                locations[i] = SyncedResourceManager.registered(array.getAsString());
            }
            System.out.println(Arrays.asList(locations));
            return new Animated(locations, SBJson.getDouble(jsonObject,"speed",1.0));
        }
        return null;
    };

    ResourceLocation getTexture(T instance);

    default int getTicks(T instance){
        if (instance instanceof EntityActor actor){
            return actor.animatedTextureTicks;
        }else
        if (instance instanceof Entity entity){
            return entity.tickCount;
        }
        return 0;
    }

    final class Default<T>  implements ModelTexture<T>{
        private ResourceLocation location;
        public Default(String path) {
            this.location = SyncedResourceManager.registered(path);
        }
        @Override
        public ResourceLocation getTexture(T instance) {
            return location;
        }
    }
    final class Animated<T> implements ModelTexture<T>{

        private ResourceLocation[] locations;
        private double speed;
        private int index = 0;
        public Animated(ResourceLocation[] locations,double speed) {
            this.locations = locations;
            this.speed = speed;
        }

        @Override
        public ResourceLocation getTexture(T instance) {
            int ticks = getTicks(instance);
            if (ticks % (1.0/speed) == 0){
                if (index < locations.length-1){
                    index++;
                }else{
                    index = 0;
                }
            }
            return locations[0];
        }
    }
}
