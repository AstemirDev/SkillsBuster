package ru.astemir.skillsbuster.common.item;


import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.astemir.api.common.register.IRegistry;
import ru.astemir.skillsbuster.SkillsBuster;

public class SBItems implements IRegistry {
    public static DeferredRegister<Item> ITEMS = IRegistry.create(SkillsBuster.MODID, ForgeRegistries.ITEMS);
    public static RegistryObject<Item> VIDEOCAM = ITEMS.register("videocam",ItemVideoCam::new);
}
