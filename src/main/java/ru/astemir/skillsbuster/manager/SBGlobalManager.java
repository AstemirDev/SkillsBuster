package ru.astemir.skillsbuster.manager;

import com.lowdragmc.shimmer.core.mixins.ShimmerMixinPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.SBClientEvents;
import ru.astemir.skillsbuster.manager.camera.SBCameraManager;
import ru.astemir.skillsbuster.manager.environmental.SBEnvironmentalManager;
import ru.astemir.skillsbuster.manager.gui.SBGuiManager;
import ru.astemir.skillsbuster.manager.model.SBModelManager;
import ru.astemir.skillsbuster.manager.resource.SyncedResourceManager;
import ru.astemir.skillsbuster.manager.keybind.SBKeyBindManager;
import ru.astemir.skillsbuster.manager.actor.SBActorManager;
import ru.astemir.skillsbuster.manager.config.SBConfigManager;
import ru.astemir.skillsbuster.manager.shader.SBShaderManager;
import ru.astemir.skillsbuster.manager.timer.ForgeRunnableHandler;

import java.util.*;

@Mod.EventBusSubscriber(modid = SkillsBuster.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SBGlobalManager {
    private LinkedHashMap<SBManager,ManagerDist> managers = new LinkedHashMap<>();
    private static SBGlobalManager instance;
    public SBGlobalManager() {
        instance = this;
        registerAll();
    }

    private void registerAll() {
        registerManager(ManagerDist.COMMON, new SBConfigManager());
        registerManager(ManagerDist.CLIENT, new SyncedResourceManager());
        registerManager(ManagerDist.CLIENT, new SBShaderManager());
        registerManager(ManagerDist.COMMON, new SBActorManager());
        registerManager(ManagerDist.SERVER, new ForgeRunnableHandler());
        registerManager(ManagerDist.CLIENT, new SBCameraManager());
        registerManager(ManagerDist.CLIENT, new SBModelManager());
        registerManager(ManagerDist.CLIENT, new SBGuiManager());
        registerManager(ManagerDist.CLIENT, new SBKeyBindManager());
        registerManager(ManagerDist.CLIENT,new SBEnvironmentalManager());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e){
        reload(e.getEntity().level);
    }

    public static void reload(Level level){
        instance.loadSide(level,ManagerDist.COMMON);
        instance.loadSide(level,ManagerDist.SERVER);
        SBClientEvents.reloadClientResourcesOnServer(level);
    }

    public void registerManager(ManagerDist dist,SBManager manager){
        this.managers.put(manager,dist);
    }

    public void loadSide(Level level,ManagerDist dist){
        managers.forEach((manager,entry)->{
            if (entry == dist){
                manager.load(level);
            }
        });
    }

    public static SBGlobalManager getInstance() {
        return instance;
    }
}
