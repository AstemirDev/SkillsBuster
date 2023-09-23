package ru.astemir.skillsbuster.common;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.astemir.skillsbuster.manager.camera.SBCameraManager;
import ru.astemir.skillsbuster.manager.actor.SBActorManager;


public class SkillsBusterCommands {


    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent e){
        SBCameraManager.commandCamera(e.getDispatcher());
        SBActorManager.commandActor(e.getDispatcher());
    }


}
