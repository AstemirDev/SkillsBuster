package ru.astemir.skillsbuster.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.astemir.api.common.register.IRegistry;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.manager.actor.EntityActor;

@Mod.EventBusSubscriber(modid = SkillsBuster.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class SBEntities implements IRegistry {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = IRegistry.create(SkillsBuster.MODID,ForgeRegistries.ENTITY_TYPES);
    public static RegistryObject<EntityType<EntityActor>> ACTOR = ENTITY_TYPES.register("actor",()->EntityType.Builder.of(EntityActor::new, MobCategory.MISC).noSummon().noSave().build("actor"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent e){
        e.put(ACTOR.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH,10).add(Attributes.MOVEMENT_SPEED, 0.5D).build());
    }
}
