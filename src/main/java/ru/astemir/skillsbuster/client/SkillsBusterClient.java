package ru.astemir.skillsbuster.client;


import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astemir.api.IClientLoader;
import org.astemir.api.common.handler.WorldEventHandler;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Rect2;
import org.astemir.api.math.components.Vector2;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.utils.RenderHelper;
import ru.astemir.skillsbuster.client.render.ActorRenderer;
import ru.astemir.skillsbuster.common.entity.SBEntities;
import ru.astemir.skillsbuster.common.utils.TextUtils;

@Mod.EventBusSubscriber(modid = SkillsBuster.MODID)
public class SkillsBusterClient implements IClientLoader {
    private static final ResourceLocation RULER_TEXTURE = new ResourceLocation("skillsbuster:textures/gui/ruler.png");
    private static boolean showRuler = false;

    @SubscribeEvent
    public static void onRenderScreen(RenderGuiOverlayEvent.Post e){
        if (showRuler) {
            Window window = e.getWindow();
            int width = window.getGuiScaledWidth();
            int height = window.getGuiScaledHeight();
            RenderHelper.blit(e.getPoseStack(),new Rect2(0,0,width,height),new Rect2(0,0,256,160),new Vector2(256,160),RULER_TEXTURE,new Color(1,1,1,0.1f));
        }
    }

    @Override
    public void load() {
        EntityRenderers.register(SBEntities.ACTOR.get(), ActorRenderer::new);
    }

    public static void setShowRuler(boolean showRuler) {
        SkillsBusterClient.showRuler = showRuler;
    }
}
