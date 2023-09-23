package ru.astemir.skillsbuster.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.astemir.api.common.handler.ClientEventHandler;
import org.astemir.api.common.handler.CustomEvent;
import org.astemir.api.common.handler.CustomEventMap;
import org.astemir.api.common.handler.WorldEventHandler;
import org.astemir.api.common.misc.ParticleEmitter;
import org.astemir.api.math.components.Vector3;
import org.astemir.api.network.PacketArgument;
import org.lwjgl.system.macosx.CGEventTapInformation;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.client.misc.InterpolationType;
import ru.astemir.skillsbuster.common.utils.ReflectionUtils;
import ru.astemir.skillsbuster.common.utils.TextUtils;
import ru.astemir.skillsbuster.manager.ManagerDist;
import ru.astemir.skillsbuster.manager.camera.SBCameraManager;
import ru.astemir.skillsbuster.manager.gui.SBGuiManager;
import ru.astemir.skillsbuster.manager.SBGlobalManager;
import ru.astemir.skillsbuster.manager.actor.EntityActor;
import ru.astemir.skillsbuster.manager.actor.SBActorManager;

public class SBClientEvents implements ClientEventHandler {
    private static final CustomEvent EVENT_RELOAD_CLIENT_RESOURCES = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_SHOW_GUI = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_HIDE_GUI = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_PLAY_PARTICLE = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_CAMERA_START = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_CAMERA_STOP = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_ZOOM_START = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_ZOOM_STOP = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_LOAD_ACTOR_CONFIGURATION = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_UPDATE_MODEL = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_SHOW_RULER = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_HIDE_RULER = CustomEventMap.createEvent();

    private static CustomEventMap clientEvents = CustomEventMap.initialize().
            registerEvent(EVENT_RELOAD_CLIENT_RESOURCES,(pos,level,args)-> {
                SBGlobalManager.getInstance().loadSide(level,ManagerDist.COMMON);
                SBGlobalManager.getInstance().loadSide(level, ManagerDist.CLIENT);
            }).
            registerEvent(EVENT_SHOW_RULER,(pos,level,args)->SkillsBusterClient.setShowRuler(true)).
            registerEvent(EVENT_HIDE_RULER,(pos,level,args)->SkillsBusterClient.setShowRuler(false)).
            registerEvent(EVENT_ZOOM_START,(pos, level, args)-> SBCameraManager.getInstance().getZoomController().zoomStart(args[0].asDouble(),args[1].asDouble(),args[2].asDouble(),ReflectionUtils.searchEnum(InterpolationType.class,args[3].asString()), ReflectionUtils.searchEnum(EasingType.class,args[4].asString()))).
            registerEvent(EVENT_ZOOM_STOP,(pos, level, args)-> SBCameraManager.getInstance().getZoomController().zoomEnd()).
            registerEvent(EVENT_SHOW_GUI,(pos,level,args)-> Minecraft.getInstance().setScreen(SBGuiManager.getInstance().get(args[0].asString()))).
            registerEvent(EVENT_HIDE_GUI,(pos,level,args)-> Minecraft.getInstance().setScreen(null)).
            registerEvent(EVENT_CAMERA_START,(pos,level,args)-> SBCameraManager.getInstance().getMotionController().startCamera(args[0].asString())).
            registerEvent(EVENT_CAMERA_STOP,(pos,level,args)-> SBCameraManager.getInstance().getMotionController().stopCamera()).
            registerEvent(EVENT_LOAD_ACTOR_CONFIGURATION,(pos,level,args)->{
                int entityId = args[0].asInt();
                String actorName = args[1].asString();
                EntityActor actor = (EntityActor) level.getEntity(entityId);
                actor.loadConfiguration(SBActorManager.getInstance().get(actorName));
            }).
            registerEvent(EVENT_UPDATE_MODEL,(pos,level,args)->{
                int entityId = args[0].asInt();
                EntityActor actor = (EntityActor) level.getEntity(entityId);
                actor.getConfiguration().model = args[1].asString();
            }).
            registerEvent(EVENT_PLAY_PARTICLE,(pos,level,args)->{
                Vector3 position = Vector3.from(args[0].asVec3());
                String particleParam = args[1].asString();
                ParticleOptions options;
                if (particleParam.equals("item")){
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(particleParam));
                    options = new ItemParticleOption(ParticleTypes.ITEM,item.getDefaultInstance());
                }else
                if (particleParam.equals("block")){
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(particleParam));
                    options = new BlockParticleOption(ParticleTypes.BLOCK, block.defaultBlockState());
                }else {
                    options = (ParticleOptions) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particleParam));
                }
                ParticleEmitter emitter = new ParticleEmitter(options);
                int count = args[2].asInt();
                Vector3 offset = Vector3.from(args[3].asVec3());
                Vector3 size = Vector3.from(args[4].asVec3());
                Vector3 speed = Vector3.from(args[5].asVec3());
                emitter.count(count);
                emitter.size(size);
                emitter.emit(level,position.add(offset),speed);
            });

    public static void registerEvents(){
        WorldEventHandler.registerClientHandler(new ResourceLocation(SkillsBuster.MODID, "client_events"),new SBClientEvents());
    }

    @Override
    public void onHandleEvent(ClientLevel level, BlockPos pos, int event, PacketArgument[] arguments) {
        clientEvents.handleEvent(event,level,pos,arguments);
    }

    public static void zoomStart(Level level,Entity entity,double value, double time,double speed, String interpolationType,String easingType){
        WorldEventHandler.playClientEvent(level,entity.blockPosition(),SBClientEvents.EVENT_ZOOM_START,PacketArgument.create(PacketArgument.ArgumentType.DOUBLE,value),PacketArgument.create(PacketArgument.ArgumentType.DOUBLE,time),PacketArgument.create(PacketArgument.ArgumentType.DOUBLE,speed),PacketArgument.str(interpolationType),PacketArgument.str(easingType));
    }

    public static void zoomStop(Level level,Entity entity){
        WorldEventHandler.playClientEvent(level,entity.blockPosition(),SBClientEvents.EVENT_ZOOM_STOP);
    }

    public static void showRuler(Level level,Entity entity){
        WorldEventHandler.playClientEvent(level,entity.blockPosition(),SBClientEvents.EVENT_SHOW_RULER);
    }

    public static void hideRuler(Level level,Entity entity){
        WorldEventHandler.playClientEvent(level,entity.blockPosition(),SBClientEvents.EVENT_HIDE_RULER);
    }

    public static void openGui(Level level,Entity entity,String guiName){
        if (entity instanceof Player player) {
            WorldEventHandler.playClientEvent(player, level, entity.blockPosition(), SBClientEvents.EVENT_SHOW_GUI, PacketArgument.str(guiName));
        }
    }

    public static void hideGui(Level level,Entity entity){
        if (entity instanceof Player player) {
            WorldEventHandler.playClientEvent(player, level, entity.blockPosition(), SBClientEvents.EVENT_SHOW_GUI);
        }
    }

    public static void cameraStart(Level level,Entity entity,String name){
        if (entity instanceof Player player) {
            WorldEventHandler.playClientEvent(player, level, entity.blockPosition(), SBClientEvents.EVENT_CAMERA_START,PacketArgument.str(name));
        }
    }

    public static void cameraStop(Level level,Entity entity){
        if (entity instanceof Player player) {
            WorldEventHandler.playClientEvent(player, level, entity.blockPosition(), SBClientEvents.EVENT_CAMERA_STOP);
        }
    }

    public static void reloadClientResourcesOnServer(Level level){
        WorldEventHandler.playClientEvent(level, BlockPos.ZERO, EVENT_RELOAD_CLIENT_RESOURCES);
    }

    public static void setActorConfiguration(Level level,EntityActor actor, String actorName){
        WorldEventHandler.playClientEvent(level,actor.blockPosition(),EVENT_LOAD_ACTOR_CONFIGURATION,PacketArgument.integer(actor.getId()),PacketArgument.str(actorName));
    }

    public static void updateModel(Level level, Vector3 position, Entity entity,String modelName){
        WorldEventHandler.playClientEvent(level,new BlockPos(position.toVec3()),SBClientEvents.EVENT_UPDATE_MODEL, PacketArgument.integer(entity.getId()),PacketArgument.str(modelName));
    }
    public static void spawnParticle(Level level,Vector3 position,String particleName,int count,Vector3 offset,Vector3 size,Vector3 speed){
        WorldEventHandler.playClientEvent(level,new BlockPos(position.toVec3()),SBClientEvents.EVENT_PLAY_PARTICLE, PacketArgument.vec3(position.toVec3()),PacketArgument.str(particleName),PacketArgument.integer(count),PacketArgument.vec3(offset.toVec3()),PacketArgument.vec3(size.toVec3()),PacketArgument.vec3(speed.toVec3()));
    }
}
