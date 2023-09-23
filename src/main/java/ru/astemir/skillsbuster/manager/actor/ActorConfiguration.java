package ru.astemir.skillsbuster.manager.actor;

import net.minecraft.world.level.Level;
import org.astemir.api.common.animation.AnimationList;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.SBClientEvents;
import ru.astemir.skillsbuster.common.entity.SBEntities;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import ru.astemir.skillsbuster.common.script.ScriptHolder;
import ru.astemir.skillsbuster.manager.NamedEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorConfiguration implements PropertyHolder, NamedEntry {
    public static final ActorConfiguration DEFAULT = new ActorConfiguration();
    @LoadProperty("scripts")
    public ScriptHolder scriptHolder = new ScriptHolder();
    @LoadProperty("animations")
    public AnimationList animations;
    @LoadProperty("bounding-box")
    public Vector2 bb = new Vector2(1,1);

    @LoadProperty("position")
    public Vector3 position = null;
    @LoadProperty("can-collide")
    public boolean canCollide = false;
    @LoadProperty("can-pickup-loot")
    public boolean canPickupLoot = false;
    @LoadProperty("health")
    public float health = 20;
    @LoadProperty("armor")
    public float armor = 0;
    @LoadProperty("speed")
    public float speed = 0.5f;
    @LoadProperty("knockback-resistance")
    public float knockbackResistance = 0;

    @LoadProperty("model")
    public String model = "default";
    @LoadProperty("morph")
    public String morphId = null;
    @LoadProperty("render-offset")
    public Vector3 renderOffset = new Vector3(0,0,0);
    @LoadProperty("render-scale")
    public Vector3 renderScale = new Vector3(1,1,1);
    @LoadProperty("render-rotation")
    public Vector3 renderRotation = new Vector3(0,0,0);
    @LoadProperty("shadow-radius")
    public float shadowRadius = 0.5f;
    @LoadProperty("shadow-strength")
    public float shadowStrength = 1.0f;
    private String name;

    public EntityActor spawn(Level level, Vector3 pos){
        EntityActor actor = SBEntities.ACTOR.get().create(level);
        actor.loadConfiguration(this);
        if (pos != null) {
            actor.setPos(pos.toVec3());
        }else{
            actor.setPos(position.toVec3());
        }
        level.addFreshEntity(actor);
        SBClientEvents.setActorConfiguration(level,actor,name);
        return actor;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}


