package ru.astemir.skillsbuster.manager.gui.nodes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Rect2;
import org.astemir.api.math.components.Vector2;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.utils.FontUtils;
import ru.astemir.skillsbuster.client.utils.RenderHelper;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.script.ScriptHolder;
import ru.astemir.skillsbuster.manager.gui.style.NamedTexture;
import ru.astemir.skillsbuster.manager.gui.style.TextureSet;
import ru.astemir.skillsbuster.manager.resource.SyncedResourceManager;

public class ButtonNode extends GuiNode{
    @LoadProperty("text")
    private String text;
    @LoadProperty("text-color")
    private Color textColor;
    @LoadProperty("style")
    private ButtonStyle buttonStyle = ButtonStyle.DEFAULT;
    @LoadProperty("state")
    private ButtonState buttonState = ButtonState.DEFAULT;
    private boolean pressed = false;

    private TextureSet<ButtonStyle,ButtonState> textureSet = new TextureSet<>().
            put(ButtonStyle.DEFAULT,new NamedTexture(new Vector2(13,13), SyncedResourceManager.registered(SkillsBuster.MODID,"textures/gui/modern.png")).
                    uv(ButtonState.DEFAULT,new Rect2(0,0,11,11)).
                    uv(ButtonState.HOVERED,new Rect2(0,43,11,11))).
            put(ButtonStyle.MINECRAFT,new NamedTexture(new Vector2(13,13), SyncedResourceManager.registered(SkillsBuster.MODID,"textures/gui/modern.png")).
                    uv(ButtonState.DEFAULT,new Rect2(48,0,11,11)).
                    uv(ButtonState.HOVERED,new Rect2(48,43,11,11)));

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean onMouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isHovered(pMouseX,pMouseY) && !pressed) {
            pressed = true;
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null) {
                onClicked(pButton);
                player.playSound(SoundEvents.LEVER_CLICK, 1, 1);
            }
            runScript("on-button-clicked");
        }
        return super.onMouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean onMouseEnter(double mouseX, double mouseY) {
        this.buttonState = ButtonState.HOVERED;
        return super.onMouseEnter(mouseX, mouseY);
    }

    @Override
    public boolean onMouseLeave(double mouseX, double mouseY) {
        this.buttonState = ButtonState.DEFAULT;
        return super.onMouseLeave(mouseX, mouseY);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (pressed) {
            pressed = false;
            onReleased(button);
            runScript("on-button-release");
        }
        return super.onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void resize(Vector2 newSize) {
        newSize = getTexture().limitSize(buttonState,newSize,2,2);
        super.resize(newSize);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Rect2 rectangle = getScaledRectangle();
        getTexture().render9x9(stack,buttonState,rectangle,getScale(),2,2,getColor());
        renderCutOutside(()->{
            if (!text.isEmpty()) {
                Font font = getFont();
                Vector2 fontSize = FontUtils.fontSize(font,text);
                RenderHelper.drawString(stack, font, text,rectangle.getPosition().add(rectangle.getSize().div(2,2)).add(new Vector2(-fontSize.x/2,-fontSize.y*0.75f)), textColor);
            }
        });
    }

    public NamedTexture<ButtonState> getTexture(){
        return textureSet.get(buttonStyle);
    }

    public void onReleased(int button){}
    public void onClicked(int button){}

    public enum ButtonStyle{
        DEFAULT,MINECRAFT
    }

    public enum ButtonState{
        DEFAULT,HOVERED
    }
}
