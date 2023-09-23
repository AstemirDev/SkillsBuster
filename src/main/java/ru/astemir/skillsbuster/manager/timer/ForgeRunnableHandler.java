package ru.astemir.skillsbuster.manager.timer;

import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.manager.SBManager;

@Mod.EventBusSubscriber(modid = SkillsBuster.MODID)
public class ForgeRunnableHandler extends SBManager.Default<ForgeRunnable> {
    private static long globalTicks = 0;
    private static ForgeRunnableHandler instance;
    public ForgeRunnableHandler() {
        instance = this;
    }
    @Override
    public void onLoad(Level level) {}

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e){
        if (e.phase == TickEvent.Phase.START){
            globalTicks++;
            for (ForgeRunnable runnable : instance.entries()) {
                if (globalTicks >= runnable.getLifeTime()){
                    runnable.run();
                    instance.remove(runnable);
                }
            }
        }
    }

    public static long getGlobalTicks() {
        return globalTicks;
    }

    public static ForgeRunnableHandler getInstance() {
        return instance;
    }
}