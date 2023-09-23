package ru.astemir.skillsbuster.common.script;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.registries.ForgeRegistries;
import org.astemir.api.common.animation.Animation;
import org.astemir.api.common.animation.AnimationList;
import org.astemir.api.common.animation.objects.IAnimatedEntity;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.SBClientEvents;
import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.client.misc.InterpolationType;
import ru.astemir.skillsbuster.common.script.argument.ParsedScriptArgs;
import ru.astemir.skillsbuster.common.script.argument.ScriptArgsList;
import ru.astemir.skillsbuster.common.script.argument.ScriptArgument;
import ru.astemir.skillsbuster.common.script.execute.ExecuteContext;
import ru.astemir.skillsbuster.common.script.execute.ScriptExecuteResult;
import ru.astemir.skillsbuster.common.script.execute.ScriptExecutor;
import ru.astemir.skillsbuster.common.script.parse.ScriptParser;
import ru.astemir.skillsbuster.common.script.parse.ScriptToken;
import ru.astemir.skillsbuster.manager.SBGlobalManager;
import ru.astemir.skillsbuster.common.utils.CommandUtils;


public enum ScriptType {

    UNKNOWN("unknown",ScriptArgsList.create(),(args,context)->{
        System.out.println("Unknown script was executed: "+args.getParser());
        return ScriptExecuteResult.NO_RESULT;
    }),

    SOUND("sound",ScriptArgsList.create(
            ScriptArgument.argString("sound"),
            ScriptArgument.argEnum("source",SoundSource.class),
            ScriptArgument.argFloat("volume"),
            ScriptArgument.argFloat("pitch")
    ), (args,context)->{
        Vector3 position = context.getPosition();
        String soundKey = args.getValue("sound");
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundKey));
        if (sound != null) {
            SoundSource soundSource = args.getValue("source",SoundSource.PLAYERS);
            float volume = args.getValue("volume",1f);
            float pitch = args.getValue("pitch",1f);
            context.getLevel().playSound(null, position.x, position.y, position.z, sound, soundSource, volume, pitch);
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    PARTICLE("particle", ScriptArgsList.create(
                    ScriptArgument.argVec3("offset"),
                    ScriptArgument.argString("particle"),
                    ScriptArgument.argInt("count"),
                    ScriptArgument.argVec3("size"),
                    ScriptArgument.argVec3("speed")),
            (args,context)->{
        SBClientEvents.spawnParticle(context.getLevel(),context.getPosition(),args.getValue("particle"),args.getValue("count",1),args.getValue("offset",new Vector3(0,0,0)),args.getValue("size",new Vector3(0,0,0)),args.getValue("speed",new Vector3(0,0,0)));
        return ScriptExecuteResult.NO_RESULT;
    }),

    MODEL("model",ScriptArgsList.create(ScriptArgument.argString("model")),(args,context)->{
        if (args.hasValue("model")) {
            for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
                if (executor.isEntity()) {
                    SBClientEvents.updateModel(context.getLevel(), context.getPosition(), executor.getEntity(), args.getValue("model"));
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    ANIMATION("animation",ScriptArgsList.create(
            ScriptArgument.argArgument("mode"),
            ScriptArgument.argString("animation"),
            ScriptArgument.argFloat("speed")),(args,context)->{
        String mode = args.getValue("mode","play").toLowerCase();
        if (args.hasValue("animation")) {
            String animationName = args.getValue("animation");
            for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
                if (executor.getEntity() != null) {
                    if (executor.getEntity() instanceof IAnimatedEntity animatedEntity) {
                        AnimationList animationList = animatedEntity.getAnimationFactory().getAnimationList();
                        Animation animation = animationList.getAnimation(animationName);
                        if (animation != null) {
                            switch (mode){
                                case "play":{
                                    animatedEntity.getAnimationFactory().play(animation);
                                    break;
                                }
                                case "stop":{
                                    animatedEntity.getAnimationFactory().stop(animation);
                                    break;
                                }
                                case "speed":{
                                    animation.speed(args.getValue("speed"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    JUMP("jump",ScriptArgsList.create(),(parser,context)->{
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            if (executor.getEntity() != null) {
                executor.getEntity();
                if (executor.getEntity() instanceof PathfinderMob mob){
                    mob.getJumpControl().jump();
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    MSG("msg",ScriptArgsList.create(),(args,context)->{
        ScriptParser parser = args.getParser();
        MutableComponent message = Component.empty();
        while(parser.hasNext()){
            ScriptToken token = parser.next();
            message = message.append(Component.literal(token.getAsString()));
        }
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            if (executor.isEntity()){
                executor.getEntity().sendSystemMessage(message);
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    GUI("gui",ScriptArgsList.create(ScriptArgument.argArgument("mode"),ScriptArgument.argString("gui")),(args,context)->{
        String mode = args.getValue("mode","show").toLowerCase();
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            if (executor.isEntity()) {
                switch (mode){
                    case "show":{
                        String guiName = args.getValue("gui");
                        SBClientEvents.openGui(context.getLevel(), executor.getEntity(), guiName);
                        break;
                    }
                    case "hide":{
                        SBClientEvents.hideGui(context.getLevel(), executor.getEntity());
                        break;
                    }
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    ZOOM_START("zoom",ScriptArgsList.create(
            ScriptArgument.argArgument("mode"),
            ScriptArgument.argDouble("fov"),
            ScriptArgument.argDouble("speed"),
            ScriptArgument.argDouble("time"),
            ScriptArgument.argEnum("interpolation", InterpolationType.class),
            ScriptArgument.argEnum("easing", EasingType.class)
    ),(args,context)->{
        String mode = args.getValue("mode","start").toLowerCase();
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            if (executor.isEntity()) {
                switch (mode){
                    case "start":{
                        double value = args.getValue("fov",40.0);
                        double speed = args.getValue("speed",1.0);
                        double time = args.getValue("time",20.0);
                        InterpolationType interpolationType = args.getValue("interpolation",InterpolationType.LINEAR);
                        EasingType easingType = args.getValue("easing",EasingType.NONE);
                        SBClientEvents.zoomStart(context.getLevel(),executor.getEntity(),value,time,speed,interpolationType.name(),easingType.name());
                        break;
                    }
                    case "stop":{
                        SBClientEvents.zoomStop(context.getLevel(),executor.getEntity());
                        break;
                    }
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    CAMERA("camera",ScriptArgsList.create(ScriptArgument.argArgument("mode"), ScriptArgument.argString("motion")),(args,context)->{
        String mode = args.getValue("mode","start").toLowerCase();
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            if (executor.isEntity()) {
                switch (mode){
                    case "start":{
                        SBClientEvents.cameraStart(context.getLevel(), executor.getEntity(), args.getValue("motion"));
                        break;
                    }
                    case "stop":{
                        SBClientEvents.cameraStop(context.getLevel(),executor.getEntity());
                        break;
                    }
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    RELOAD_ALL("reload_all",ScriptArgsList.create(),(args,context)->{
        SBGlobalManager.reload(context.getLevel());
        return ScriptExecuteResult.NO_RESULT;
    }),

    RULER("ruler",ScriptArgsList.create(ScriptArgument.argArgument("mode")),(args,context)->{
        String mode = args.getValue("mode","show").toLowerCase();
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            if (executor.isEntity()){
                switch (mode){
                    case "show":{
                        SBClientEvents.showRuler(context.getLevel(),executor.getEntity());
                        break;
                    }
                    case "hide":{
                        SBClientEvents.hideRuler(context.getLevel(),executor.getEntity());
                        break;
                    }
                }
            }
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    CMD("cmd",ScriptArgsList.create(ScriptArgument.argString("cmd")),(args,context)->{
        String command = args.getValue("cmd","");
        for (ScriptExecutor executor : context.getExecutorList().getExecutors()) {
            CommandUtils.runCommand(context.getLevel(),context.getPosition(),context.getRotation(),command,executor.getEntity());
        }
        return ScriptExecuteResult.NO_RESULT;
    }),

    WAIT("wait",ScriptArgsList.create(ScriptArgument.argInt("delay")),(args, context)-> ScriptExecuteResult.delay(args.getValue("delay",0)));

    private Executable executable;
    private ScriptArgsList argsList;
    private String keyword;
    

    ScriptType(String keyword,ScriptArgsList argsList,Executable executable) {
        this.keyword = keyword;
        this.argsList = argsList;
        this.executable = executable;
    }

    public String getKeyword() {
        return keyword;
    }

    public Executable getExecutable() {
        return executable;
    }

    public ScriptArgsList getArgsList() {
        return argsList;
    }

    public static ScriptType byKeyword(String keyword){
        for (ScriptType value : values()) {
            if (value.getKeyword().equals(keyword)){
                return value;
            }
        }
        return UNKNOWN;
    }

    public interface Executable{
        ScriptExecuteResult onExecute(ParsedScriptArgs args, ExecuteContext context);
    }

}
