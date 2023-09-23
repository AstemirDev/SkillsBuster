package ru.astemir.skillsbuster.manager.gui.nodes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Rect2;
import org.astemir.api.math.components.Vector2;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.utils.RenderHelper;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.manager.gui.style.NamedTexture;

public class WindowNode extends ContainerNode{
    @LoadProperty("background-color")
    private Color backgroundColor = new Color(0.1f,0.1f,0.1f,1);
    @LoadProperty("show-background")
    private boolean showBackground = true;
    private boolean selected = false;
    private NamedTexture<WindowState> texture = new NamedTexture(Vector2.create(19,19),new ResourceLocation(SkillsBuster.MODID,"textures/gui/modern.png")).
            uv(WindowState.DEFAULT,Rect2.rect(0,120,16,16));

    private WindowState windowState = WindowState.DEFAULT;
    @Override
    public void resize(Vector2 newSize) {
        newSize = texture.limitSize(windowState,newSize,2,2);
        super.resize(newSize);
    }

    @Override
    public boolean onMouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isHovered(pMouseX,pMouseY)){
            selected = true;
        }
        return super.onMouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean onMouseReleased(double pMouseX, double pMouseY, int pButton) {
        selected = false;
        return super.onMouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean onMouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int windowBorder = 8;
        if (isSelected()) {
            Rect2 windowRect = getScaledRectangle();
            if (pMouseY < windowRect.getY()+windowBorder){
                setPosition(getPosition().add((float) pDragX, (float) pDragY));
            }
        }
        return super.onMouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY,float partialTicks) {
        Rect2 rectangle = getScaledRectangle();
        Rect2 uv = texture.getUV(windowState);
        Vector2 size = texture.limitSize(windowState,rectangle.getSize(),2,2);
        if (showBackground) {
            RenderHelper.fillColor(stack, new Rect2(rectangle.getX(),rectangle.getY(), size.x,size.y), backgroundColor);
        }
        renderCutOutside(()->renderChildren(stack,mouseX,mouseY,partialTicks),new Rect2(new Vector2(0,0),uv.getSize().mul(-1)));
        texture.render9x9(stack,windowState,rectangle,getScale(),2,2,getColor());
    }

    @Override
    public Vector2 getChildrenOffset() {
        Rect2 uv = texture.getUV(windowState);
        return new Vector2(uv.getWidth()/2,uv.getHeight()/2);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isShowBackground() {
        return showBackground;
    }

    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }

    public boolean isSelected() {
        return selected;
    }

    public enum WindowState{DEFAULT}
}
