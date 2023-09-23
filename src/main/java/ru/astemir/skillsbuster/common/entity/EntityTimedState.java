package ru.astemir.skillsbuster.common.entity;


import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.astemir.api.common.animation.Animation;
import org.astemir.api.common.animation.objects.IAnimatedEntity;
import org.astemir.api.math.components.Vector3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityTimedState {

    public int hurtTime;
    public float animationPosition;
    public float animationSpeed;
    public float animationSpeedOld;
    public int deathTime;
    public float attackAnim;
    public float oAttackAnim;
    public float yBodyRot;
    public float yBodyRotO;
    public float yHeadRot;
    public float yHeadRotO;
    public boolean hasImpulse;
    public float fallDistance;
    public float xRotO;
    public float xRot;
    public boolean hurtMarked;
    private Vec3 position;
    private boolean isOnGround;
    private List<Pair<EquipmentSlot,ItemStack>> equipment = new ArrayList<>();
    private Set<String> animations = new HashSet<>();
    private CompoundTag nbt;
    private Entity vehicle;
    private Vector3 customPosition = new Vector3(0,0,0);
    private Vector3 customScale = new Vector3(1,1,1);
    private Vector3 customRotation = new Vector3(0,0,0);


    public EntityTimedState(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            equipment.add(new Pair<>(EquipmentSlot.MAINHAND,livingEntity.getItemBySlot(EquipmentSlot.MAINHAND)));
            equipment.add(new Pair<>(EquipmentSlot.OFFHAND,livingEntity.getItemBySlot(EquipmentSlot.OFFHAND)));
            equipment.add(new Pair<>(EquipmentSlot.HEAD,livingEntity.getItemBySlot(EquipmentSlot.HEAD)));
            equipment.add(new Pair<>(EquipmentSlot.CHEST,livingEntity.getItemBySlot(EquipmentSlot.CHEST)));
            equipment.add(new Pair<>(EquipmentSlot.LEGS,livingEntity.getItemBySlot(EquipmentSlot.LEGS)));
            equipment.add(new Pair<>(EquipmentSlot.FEET,livingEntity.getItemBySlot(EquipmentSlot.FEET)));

            hurtTime = livingEntity.hurtTime;
            animationPosition = livingEntity.animationPosition;
            animationSpeed = livingEntity.animationSpeed;
            animationSpeedOld = livingEntity.animationSpeedOld;
            deathTime = livingEntity.deathTime;
            attackAnim = livingEntity.attackAnim;
            oAttackAnim = livingEntity.oAttackAnim;
            yBodyRot = livingEntity.yBodyRot;
            yHeadRot = livingEntity.yHeadRot;
            yBodyRotO = livingEntity.yBodyRotO;
            yHeadRotO = livingEntity.yHeadRotO;
        }
        position = entity.trackingPosition();
        isOnGround = entity.isOnGround();
        hasImpulse = entity.hasImpulse;
        fallDistance = entity.fallDistance;
        xRotO = entity.xRotO;
        xRot = entity.getXRot();
        hurtMarked = entity.hurtMarked;
        vehicle = entity.getVehicle();
    }

    public void apply(Entity entity){
        entity.hasImpulse = hasImpulse;
        entity.fallDistance = fallDistance;
        entity.hurtMarked = hurtMarked;
        entity.setXRot(xRot);
        entity.xRotO = xRotO;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            for (Pair<EquipmentSlot, ItemStack> equipmentSlotItemStackPair : equipment) {
                livingEntity.setItemSlot(equipmentSlotItemStackPair.getFirst(),equipmentSlotItemStackPair.getSecond());
            }
            livingEntity.hurtTime = hurtTime;
            livingEntity.animationPosition = animationPosition;
            livingEntity.animationSpeed = animationSpeed;
            livingEntity.animationSpeedOld = animationSpeedOld;
            livingEntity.deathTime = deathTime;
            livingEntity.attackAnim = attackAnim;
            livingEntity.oAttackAnim = oAttackAnim;
            livingEntity.yHeadRot = yHeadRot;
            livingEntity.yHeadRotO = yHeadRotO;
        }
    }

    public void applyClient(Entity entity){
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.hurtTime = hurtTime;
            livingEntity.deathTime = deathTime;
            livingEntity.attackAnim = attackAnim;
            livingEntity.oAttackAnim = oAttackAnim;
        }
        entity.hasImpulse = hasImpulse;
        entity.hurtMarked = hurtMarked;
    }

    public void syncAnimations(Entity entity){
        if (entity instanceof IAnimatedEntity){
            IAnimatedEntity animated = (IAnimatedEntity) entity;
            for (Animation playingAnimation : animated.getAnimationFactory().getPlayingAnimations()) {
                if (!animations.contains(playingAnimation.getName())){
                    animated.getAnimationFactory().removeAnimation(playingAnimation);
                }
            }
            for (String animationName : animations) {
                Animation animation = animated.getAnimationFactory().getAnimationList().getAnimation(animationName);
                if (animation != null) {
                    animated.getAnimationFactory().addAnimation(animation);
                }
            }
        }
    }

    public Vector3 getCustomPosition() {
        return customPosition;
    }

    public void setCustomPosition(Vector3 customPosition) {
        this.customPosition = customPosition;
    }

    public Vector3 getCustomScale() {
        return customScale;
    }

    public void setCustomScale(Vector3 customScale) {
        this.customScale = customScale;
    }

    public Vector3 getCustomRotation() {
        return customRotation;
    }

    public void setCustomRotation(Vector3 customRotation) {
        this.customRotation = customRotation;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public Set<String> getAnimations() {
        return animations;
    }

    public void setAnimations(Set<String> animations) {
        this.animations = animations;
    }

    public List<Pair<EquipmentSlot, ItemStack>> getEquipment() {
        return equipment;
    }

    public float getYBodyRot() {
        return yBodyRot;
    }

    public float getYHeadRot() {
        return yHeadRot;
    }

    public float getXRot() {
        return xRot;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Entity getVehicle() {
        return vehicle;
    }
}
