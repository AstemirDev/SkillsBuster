package ru.astemir.skillsbuster.manager.environmental;

import com.google.gson.JsonObject;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astemir.api.client.event.GlobalRenderEvent;
import org.astemir.api.client.event.SkyRenderEvent;
import org.astemir.api.client.event.SkySetupEvent;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Vector2;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import ru.astemir.skillsbuster.manager.config.SBConfigValue;
import ru.astemir.skillsbuster.manager.gui.SBGui;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillsBuster.MODID,value = Dist.CLIENT)
public class SBEnvironmentalManager extends SBManager.Configurable<Object> {
    private static SBEnvironmentalManager instance;
    public static SBConfigValue<Color> fogColor;
    public static SBConfigValue<Color> cloudColor;
    public static SBConfigValue<Color> skyColor;
    public static SBConfigValue<Color> sunriseColor;
    public static SBConfigValue<Color> celestialsColor;
    public static SBConfigValue<Vector2> celestialsRotation;
    public static SBConfigValue<Float> sunVerticalOffset;
    public static SBConfigValue<Float> moonVerticalOffset;
    public static SBConfigValue<Float> sunSize;
    public static SBConfigValue<Float> moonSize;
    public static SBConfigValue<FogShape> fogShape;
    public static SBConfigValue<Float> fogFarDistance;
    public static SBConfigValue<Float> fogNearDistance;
    public static SBConfigValue<Float> darkness;
    public static SBConfigValue<Float> starBrightness;
    public static SBConfigValue<Boolean> blockOutline;
    public static SBConfigValue<Boolean> renderHand;

    public SBEnvironmentalManager() {
        super(ConfigType.ENVIRONMENTAL);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        for (SBConfig configuration : configurations) {
            JsonObject json = configuration.getFile().json();
            moonVerticalOffset = new SBConfigValue<>("moon-vertical-offset",json,SBJson::getFloat);
            sunVerticalOffset = new SBConfigValue<>("sun-vertical-offset",json,SBJson::getFloat);
            sunSize = new SBConfigValue<>("sun-size",json,SBJson::getFloat);
            moonSize = new SBConfigValue<>("moon-size",json,SBJson::getFloat);
            celestialsRotation = new SBConfigValue<>("celestial-rotation",json,Vector2.class,SBJson::get);
            celestialsColor = new SBConfigValue<>("celestial-color", json, Color.class, SBJson::get);
            cloudColor = new SBConfigValue<>("clouds-color", json, Color.class, SBJson::get);
            fogColor = new SBConfigValue<>("fog-color", json, Color.class, SBJson::get);
            skyColor = new SBConfigValue<>("sky-color", json, Color.class, SBJson::get);
            sunriseColor = new SBConfigValue<>("sunrise-color", json, Color.class, SBJson::get);
            fogShape = new SBConfigValue<>("fog-shape", json, FogShape.class, SBJson::getEnum);
            fogNearDistance = new SBConfigValue<>("fog-near", json, SBJson::getFloat);
            fogFarDistance = new SBConfigValue<>("fog-far", json, SBJson::getFloat);
            darkness = new SBConfigValue<>("darkness", json, SBJson::getFloat);
            starBrightness = new SBConfigValue<>("stars-brightness", json, SBJson::getFloat);
            blockOutline = new SBConfigValue<>("block-outline",json,SBJson::getBoolean,renderer::setRenderBlockOutline);
            renderHand = new SBConfigValue<>("render-hand",json,SBJson::getBoolean,renderer::setRenderHand);
        }
    }


    @SubscribeEvent
    public static void onSkyRender(SkyRenderEvent e){
        if (moonVerticalOffset != null && moonVerticalOffset.isChanged()) {
            e.setMoonVerticalOffset(moonVerticalOffset.getValue());
        }
        if (sunVerticalOffset != null && sunVerticalOffset.isChanged()){
            e.setSunVerticalOffset(sunVerticalOffset.getValue());
        }
        if (sunSize != null && sunSize.isChanged()){
            e.setSunSize(sunSize.getValue());
        }
        if (moonSize != null && moonSize.isChanged()) {
            e.setMoonSize(moonSize.getValue());
        }
        if (celestialsRotation != null && celestialsRotation.isChanged()) {
            Vector2 rotation = celestialsRotation.getValue();
            e.setXRot(rotation.x);
            e.setYRot(rotation.y);
        }
        if (celestialsColor != null && celestialsColor.isChanged()) {
            e.setColor(celestialsColor.getValue());
        }
    }

    @SubscribeEvent
    public static void onSetupDarkness(SkySetupEvent.ComputeDarkness e){
        if (darkness != null && darkness.isChanged()){
            e.setDarkness(darkness.getValue());
        }
    }

    @SubscribeEvent
    public static void onSetupStarBrightness(SkySetupEvent.ComputeStarBrightness e){
        if (starBrightness != null && starBrightness.isChanged()){
            e.setBrightness(starBrightness.getValue());
        }
    }


    @SubscribeEvent
    public static void onSetupSunriseColor(SkySetupEvent.ComputeSunriseColor e){
        if (sunriseColor != null && sunriseColor.isChanged()){
            Color color = sunriseColor.getValue();
            e.setSunriseColor(new float[]{color.r,color.g,color.b,color.a});
        }
    }

    @SubscribeEvent
    public static void onSetupSkyColor(SkySetupEvent.ComputeSkyColor e){
        if (skyColor != null && skyColor.isChanged()){
            e.setColor(skyColor.getValue().toVec3());
        }
    }

    @SubscribeEvent
    public static void onSetupCloudColor(SkySetupEvent.ComputeCloudColor e){
        if (cloudColor != null && cloudColor.isChanged()) {
            e.setColor(cloudColor.getValue().toVec3());
        }
    }

    @SubscribeEvent
    public static void onFogRender(ViewportEvent.RenderFog e){
        boolean cancel = false;
        if (fogFarDistance != null && fogFarDistance.isChanged()){
            cancel = true;
            e.setFarPlaneDistance(fogFarDistance.getValue());
        }
        if (fogNearDistance != null && fogNearDistance.isChanged()){
            cancel = true;
            e.setNearPlaneDistance(fogNearDistance.getValue());
        }
        if (fogShape != null && fogShape.isChanged()) {
            cancel = true;
            e.setFogShape(fogShape.getValue());
        }
        if (cancel){
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor e){
        if (fogColor != null && fogColor.isChanged()) {
            Color color = fogColor.getValue();
            e.setRed(color.r);
            e.setGreen(color.g);
            e.setBlue(color.b);
        }
    }

    public static SBEnvironmentalManager getInstance() {
        return instance;
    }
}
