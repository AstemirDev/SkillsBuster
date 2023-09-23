package ru.astemir.skillsbuster.manager.camera;


import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.compress.utils.FileNameUtils;
import org.astemir.api.client.event.CameraAdvancedSetupEvent;
import org.astemir.api.client.event.CameraPreMatrixResetEvent;
import org.astemir.api.common.commands.build.CommandArgument;
import org.astemir.api.common.commands.build.CommandBuilder;
import org.astemir.api.common.commands.build.CommandPart;
import org.astemir.api.common.commands.build.CommandVariant;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.misc.MouseMovement;
import ru.astemir.skillsbuster.common.io.ModFile;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.camera.motion.CameraMotion;
import ru.astemir.skillsbuster.manager.camera.motion.CameraMotionController;
import ru.astemir.skillsbuster.manager.camera.zoom.ZoomController;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.manager.config.SBConfigValue;
import java.io.File;
import java.util.List;

@Mod.EventBusSubscriber(modid = SkillsBuster.MODID,value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SBCameraManager extends SBManager.Configurable<CameraMotion> {
    public static final SuggestionProvider<CommandSourceStack> MOTIONS = SuggestionProviders.register(new ResourceLocation(SkillsBuster.MODID,"motions"), (context, builder)-> SharedSuggestionProvider.suggest(SBCameraManager.getInstance().entriesNames(), builder));
    private static SBCameraManager instance;
    private CameraMotionController motionController = new CameraMotionController();
    private ZoomController zoomController = new ZoomController();
    private SBConfigValue<Vector3> configuredRotation;
    private SBConfigValue<Vector3> configuredOffset;
    private SBConfigValue<Double> configuredFov;
    private SBConfigValue<Double> configuredCameraSpeed;
    private File folder;
    private double fov = -1;

    public SBCameraManager() {
        super(ConfigType.CAMERA);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        for (SBConfig configuration : configurations) {
            JsonObject cameraConfigJson = configuration.getFile().json();
            configuredRotation = new SBConfigValue<>("rotation",cameraConfigJson, Vector3.class,SBJson::get);
            configuredOffset = new SBConfigValue<>("offset",cameraConfigJson, Vector3.class,SBJson::get);
            configuredCameraSpeed = new SBConfigValue<>("speed",cameraConfigJson,SBJson::getDouble);
            configuredFov = new SBConfigValue<>("fov",cameraConfigJson,SBJson::getDouble);
            zoomController.setZoomSpeed(SBJson.getDouble(cameraConfigJson,"zoom-speed", 0.3f));
            zoomController.setSmoothCameraOnZoom(SBJson.getBoolean(cameraConfigJson,"zoom-smooth", true));
            String path = SBJson.getString(cameraConfigJson,"motions");
            folder = FMLPaths.GAMEDIR.get().resolve(path).toFile();
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (String fileName : folder.list()) {
                ModFile motionFile = new ModFile(path + "/" + fileName, false, false);
                CameraMotion cameraMotion = SBJson.as(motionFile.json(), CameraMotion.class);
                add(FileNameUtils.getBaseName(fileName), cameraMotion);
            }
        }
    }


    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent e){
        SBCameraManager cameraManager = getInstance();
        if (e.phase == TickEvent.Phase.START){
            cameraManager.motionController.tick(e.renderTickTime);
            cameraManager.zoomController.tick(e.renderTickTime);
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(CameraAdvancedSetupEvent e){
        SBCameraManager cameraManager = getInstance();
        cameraManager.motionController.cameraUpdate(e);
        if (cameraManager.configuredOffset.isChanged()) {
            Vector3 offset = cameraManager.configuredOffset.getValue();
            e.offset(offset.x, offset.y, offset.z);
        }
    }


    @SubscribeEvent
    public static void onCameraSetupAngles(ViewportEvent.ComputeCameraAngles e){
        SBCameraManager cameraManager = getInstance();
        if (!cameraManager.motionController.cameraUpdateRot(e)){
            if (cameraManager.configuredRotation.isChanged()){
                Vector3 rotation = cameraManager.configuredRotation.getValue();
                e.setPitch(rotation.x);
                e.setYaw(rotation.y);
                e.setRoll(rotation.z);
            }
        }
    }

    @SubscribeEvent
    public static void onCameraSetupFov(ViewportEvent.ComputeFov e){
        SBCameraManager cameraManager = getInstance();
        cameraManager.fov = e.getFOV();
        if (!cameraManager.motionController.cameraUpdateFov(e)){
            cameraManager.zoomController.fovUpdate(e);
        }
    }

    @SubscribeEvent
    public static void onCameraPreReset(CameraPreMatrixResetEvent e){
        SBCameraManager cameraManager = getInstance();
        cameraManager.motionController.cameraUpdateScale(e);
    }

    public static void cameraTurn(MouseMovement mouseMovement) {
        SBCameraManager cameraManager = getInstance();
        float rotX = mouseMovement.getRotX();
        float rotY = mouseMovement.getRotY();
        if (cameraManager.configuredCameraSpeed.isChanged()) {
            double cameraSpeed = cameraManager.configuredCameraSpeed.getValue();
            rotX = (float) (rotX * cameraSpeed);
            rotY = (float) (rotY * cameraSpeed);
        }
        if (cameraManager.zoomController.isZooming()){
            double zoomSpeed = cameraManager.getZoomController().getZoomSpeed();
            rotX = (float) (rotX*zoomSpeed);
            rotY = (float) (rotY*zoomSpeed);
        }
        mouseMovement.setRotX(rotX);
        mouseMovement.setRotY(rotY);
    }

    public static void commandCamera(CommandDispatcher<CommandSourceStack> dispatcher){
        CommandBuilder command = new CommandBuilder("camera");
        CommandArgument motionArg = CommandArgument.string("motion").suggestion(MOTIONS);
        command.variants(
                new CommandVariant(CommandPart.create("start"),motionArg).execute((p)->{
                    SBCameraManager.getInstance().motionController.startCamera(motionArg.getString(p));
                    return 1;
                }),
                new CommandVariant(CommandPart.create("stop")).execute((p)->{
                    SBCameraManager.getInstance().motionController.stopCamera();
                    return 1;
                })
        );
        dispatcher.register(command.permission((p)->p.hasPermission(2)).build());
    }


    public double getFov(){
        if (configuredFov.isChanged()){
            return configuredFov.getValue();
        }else{
            return fov;
        }
    }

    public ZoomController getZoomController() {
        return zoomController;
    }

    public File getFolder() {
        return folder;
    }

    public CameraMotionController getMotionController() {
        return motionController;
    }

    public static SBCameraManager getInstance() {
        return instance;
    }
}
