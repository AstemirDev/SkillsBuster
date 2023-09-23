package ru.astemir.skillsbuster.common;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.astemir.api.common.handler.CustomEvent;
import org.astemir.api.common.handler.CustomEventMap;
import org.astemir.api.common.handler.ServerEventHandler;
import org.astemir.api.common.handler.WorldEventHandler;
import org.astemir.api.network.PacketArgument;
import org.lwjgl.system.macosx.CGEventTapInformation;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.common.script.ScriptNetHandler;
import ru.astemir.skillsbuster.common.utils.TextUtils;
import ru.astemir.skillsbuster.manager.ManagerDist;
import ru.astemir.skillsbuster.manager.SBGlobalManager;

import java.util.ArrayList;
import java.util.List;

public class SBServerEvents implements ServerEventHandler {

    private static final CustomEvent EVENT_SCRIPT_TO_SERVER = CustomEventMap.createEvent();

    private static CustomEventMap serverEvents = CustomEventMap.initialize().
            registerEvent(EVENT_SCRIPT_TO_SERVER,(pos,level,args)-> {
                int id = args[0].asInt();
                Entity entity;
                if (id != -1) {
                    entity = level.getEntity(args[0].asInt());
                } else {
                    entity = null;
                }
                List<String> scripts = new ArrayList<>();
                for (int i = 1;i<args.length;i++){
                    scripts.add(args[i].asString());
                }
                ScriptNetHandler.executeScripts(level,entity,scripts);
            });

    public static void registerEvents(){
        WorldEventHandler.registerServerHandler(new ResourceLocation(SkillsBuster.MODID, "server_events"),new SBServerEvents());
    }

    @Override
    public void onHandleEvent(ServerLevel level, BlockPos pos, int event, PacketArgument[] arguments) {
        serverEvents.handleEvent(event,level,pos,arguments);
    }

    public static void runScript(Level level, BlockPos pos, PacketArgument[] args){
        WorldEventHandler.playServerEvent(level,pos,EVENT_SCRIPT_TO_SERVER,args);
    }
}
