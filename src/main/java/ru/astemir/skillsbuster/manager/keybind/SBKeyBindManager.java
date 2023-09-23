package ru.astemir.skillsbuster.manager.keybind;


import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import ru.astemir.skillsbuster.common.script.ScriptNetHandler;

import java.util.List;


@Mod.EventBusSubscriber(modid = SkillsBuster.MODID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SBKeyBindManager extends SBManager.Configurable<SBKeyBind> {

    private static SBKeyBindManager instance;
    public SBKeyBindManager() {
        super(ConfigType.KEYBINDS);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        for (SBConfig configuration : configurations) {
            JsonObject keybindingsJson = configuration.getFile().getJsonObject("keybindings");
            for (String name : keybindingsJson.keySet()) {
                add(PropertyHolder.buildHolder(SBKeyBind.class, keybindingsJson.get(name)));
            }
        }
    }


    @SubscribeEvent
    public static void onHandle(TickEvent.ClientTickEvent e){
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (e.phase == TickEvent.Phase.START){
            for (SBKeyBind bind : instance.entries()) {
                if (bind.isActive()){
                    ScriptNetHandler.sendScriptsToServer(player.level,player,player.blockPosition(),bind.getScripts());
                }
            }
        }
    }


    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key e){
        input(e.getKey(),e.getAction());
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton e){
        input(toKey(e.getButton()),e.getAction());
    }

    private static void input(int key,int action){
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        for (SBKeyBind bind : instance.entries()) {
            if (screen != null && !bind.isIgnoreMenus()) {
                continue;
            }
            if (bind.getKey().getValue() == key) {
                switch (action) {
                    case InputConstants.PRESS -> bind.setPressed(true);
                    case InputConstants.RELEASE -> bind.setPressed(false);
                }
            }
        }
    }

    private static int toKey(int key){
        switch (key){
            case GLFW.GLFW_MOUSE_BUTTON_LEFT:{
                return InputConstants.MOUSE_BUTTON_LEFT;
            }
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT:{
                return InputConstants.MOUSE_BUTTON_RIGHT;
            }
            case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:{
                return InputConstants.MOUSE_BUTTON_MIDDLE;
            }
        }
        return -1;
    }

    public static SBKeyBindManager getInstance() {
        return instance;
    }
}
