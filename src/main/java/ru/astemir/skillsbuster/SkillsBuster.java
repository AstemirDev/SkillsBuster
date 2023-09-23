package ru.astemir.skillsbuster;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.astemir.api.IClientLoader;
import org.astemir.api.SkillsForgeMod;
import org.astemir.api.common.event.EventManager;
import org.astemir.api.common.register.IRegistry;
import ru.astemir.skillsbuster.client.SBClientEvents;
import ru.astemir.skillsbuster.client.SkillsBusterClient;
import ru.astemir.skillsbuster.common.SBServerEvents;
import ru.astemir.skillsbuster.manager.SBGlobalManager;
import ru.astemir.skillsbuster.common.entity.SBEntities;
import ru.astemir.skillsbuster.common.SkillsBusterCommands;
import ru.astemir.skillsbuster.common.item.SBItems;

@Mod(SkillsBuster.MODID)
public class SkillsBuster extends SkillsForgeMod {

    public static final String MODID = "skillsbuster";
    private static SkillsBuster instance;
    private SBGlobalManager globalManager;

    public SkillsBuster() {
        this.instance = this;
        this.globalManager = new SBGlobalManager();
        SBClientEvents.registerEvents();
        SBServerEvents.registerEvents();
        IRegistry.register(SBEntities.ENTITY_TYPES);
        IRegistry.register(SBItems.ITEMS);
    }

    @Override
    protected void onCommonSetup(FMLCommonSetupEvent event) {
        EventManager.registerForgeEventClass(SkillsBusterCommands.class);
    }

    public static SkillsBuster getInstance() {
        return instance;
    }
    @Override
    public IClientLoader getClientLoader() {
        return new SkillsBusterClient();
    }
}
