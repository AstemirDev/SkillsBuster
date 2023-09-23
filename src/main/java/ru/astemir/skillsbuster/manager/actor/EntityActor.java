package ru.astemir.skillsbuster.manager.actor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.common.animation.AnimationFactory;
import org.astemir.api.common.animation.objects.IAnimatedEntity;
import org.astemir.api.common.entity.utils.EntityUtils;
import org.astemir.api.common.misc.ICustomRendered;

import java.util.concurrent.CopyOnWriteArrayList;

public class EntityActor extends PathfinderMob implements ICustomRendered, IAnimatedEntity {

    public static final CopyOnWriteArrayList<EntityActor> ACTORS = new CopyOnWriteArrayList<>();
    private ActorConfiguration configuration = ActorConfiguration.DEFAULT;
    private AnimationFactory animationFactory = new AnimationFactory(this);
    @OnlyIn(Dist.CLIENT)
    public Entity morph;
    @OnlyIn(Dist.CLIENT)
    public int animatedTextureTicks = 0;

    public EntityActor(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        ACTORS.add(this);
    }

    public void loadConfiguration(ActorConfiguration configuration) {
        this.configuration = configuration;
        if (configuration.animations != null) {
            this.animationFactory = new AnimationFactory(this);
            this.animationFactory.setAnimationList(configuration.animations);
        }
        if (configuration.position != null){
            setPos(configuration.position.toVec3());
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(configuration.health);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(configuration.knockbackResistance);
        this.getAttribute(Attributes.ARMOR).setBaseValue(configuration.armor);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(configuration.speed);
        this.setHealth(configuration.health);
        this.refreshDimensions();
        if (!level.isClientSide) {
            runScript("on-load");
        }
    }

//    public void track(){
//        Entity tracked = getTrackedEntity();
//        if (tracked != null) {
//            xRotO = tracked.xRotO;
//            yRotO = tracked.yRotO;
//            setPose(tracked.getPose());
//            setSprinting(tracked.isSprinting());
//            setPos(tracked.getX(),tracked.getY(),tracked.getZ());
//            setXRot(tracked.getXRot());
//            setYRot(tracked.getYRot());
//            MinecraftServer server = level.getServer();
//            server.getPlayerList().broadcastAll(new ClientboundTeleportEntityPacket(this));
//            server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, (byte) Mth.floor(tracked.getYHeadRot() * 256.0F / 360.0F)));
//            if (tracked instanceof LivingEntity livingTracked){
//                List<Pair<EquipmentSlot, ItemStack>> equipment = new ArrayList<>();
//                for (EquipmentSlot value : EquipmentSlot.values()) {
//                    equipment.add(new Pair<>(value,livingTracked.getItemBySlot(value)));
//                }
//                server.getPlayerList().broadcastAll(new ClientboundSetEquipmentPacket(this.getId(), equipment));
//                setYHeadRot(livingTracked.getYHeadRot());
//                yHeadRot = livingTracked.yHeadRot;
//                yHeadRotO = livingTracked.yHeadRotO;
//            }
//        }
//    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        super.tick();
        animatedTextureTicks++;
        if (getVehicle() != null){
            runScript("on-ride");
        }
        if (EntityUtils.isMoving(this,-0.1f,0.1f)){
            runScript("on-move");
            if (!isOnGround()) {
                runScript("on-fall");
            }else
            if (isCrouching()){
                runScript("on-ground");
                runScript("on-crouch-walk");
            }else
            if (isSprinting()){
                runScript("on-ground");
                if (getVehicle() != null) {
                    runScript("on-ride-run");
                }else{
                    runScript("on-run");
                }
            }else{
                runScript("on-ground");
                if (getVehicle() != null) {
                    runScript("on-ride_walk");
                }else{
                    runScript("on-walk");
                }
            }
        }else{
            if (!isOnGround()) {
                runScript("on-fall");
            }else
            if (isCrouching()){
                runScript("on-ground");
                runScript("on-crouch-idle");
            }else
            if (getVehicle() != null){
                runScript("on-ground");
                runScript("on-ride-idle");
            }else{
                runScript("on-ground");
                runScript("on-idle");
            }
        }
        runScript("on-tick");
    }

    @Override
    public void die(DamageSource pDamageSource) {
        runScript("on-death");
        super.die(pDamageSource);
    }

    @Override
    public void onRemovedFromWorld() {
        ACTORS.remove(this);
        super.onRemovedFromWorld();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        runScript("on-attack");
        return super.doHurtTarget(pEntity);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        runScript("on-hurt");
        return super.hurt(pSource, pAmount);
    }

    public void runScript(String script){
        if (configuration != null){
            configuration.scriptHolder.runScript(level,this,script);
        }
    }

    @Override
    public boolean isPushable() {
        return configuration.canCollide;
    }

    @Override
    public void push(Entity pEntity) {
        if (configuration.canCollide) {
            super.push(pEntity);
        }
    }

    @Override
    protected void pushEntities() {
        if (configuration.canCollide) {
            super.pushEntities();
        }
    }

    @Override
    protected void doPush(Entity pEntity) {
        if (configuration.canCollide) {
            super.doPush(pEntity);
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return configuration.canPickupLoot;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.fixed(configuration.bb.x,configuration.bb.y);
    }


    @Override
    public <K extends IDisplayArgument> AnimationFactory getAnimationFactory(K argument) {
        return animationFactory;
    }

    public ActorConfiguration getConfiguration() {
        return configuration;
    }
}
