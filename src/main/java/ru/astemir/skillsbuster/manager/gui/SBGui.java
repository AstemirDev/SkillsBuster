package ru.astemir.skillsbuster.manager.gui;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.astemir.skillsbuster.manager.NamedEntry;
import ru.astemir.skillsbuster.manager.gui.nodes.ContainerNode;
import ru.astemir.skillsbuster.manager.gui.nodes.GuiNode;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;

import java.util.ArrayList;
import java.util.List;


public class SBGui extends Screen implements PropertyHolder, NamedEntry {
    @LoadProperty("children")
    private List<GuiNode> children = new ArrayList<>();
    @LoadProperty("font")
    private Font font = Minecraft.getInstance().font;

    private String name;
    private long ticks = 0;
    public SBGui() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        for (GuiNode child : children) {
            child.setGui(this);
            child.init();
        }
    }

    @Override
    public void tick() {
        ticks++;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float pPartialTick) {
        for (GuiNode child : children) {
            child.checkHoverState(mouseX,mouseY);
            if (child.isVisible()) {
                child.render(poseStack, mouseX, mouseY, pPartialTick);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (GuiNode child : children) {
            child.onMouseClicked(pMouseX,pMouseY,pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (GuiNode child : children) {
            child.onMouseReleased(pMouseX,pMouseY,pButton);
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for (GuiNode child : children) {
            child.onMouseDragged(pMouseX,pMouseY,pButton,pDragX,pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (GuiNode child : children) {
            child.onMouseScrolled(pMouseX,pMouseY,pDelta);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (GuiNode child : children) {
            child.onKeyReleased(pKeyCode, pScanCode, pModifiers);
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        for (GuiNode child : children) {
            child.onCharTyped(pCodePoint, pModifiers);
        }
        return super.charTyped(pCodePoint, pModifiers);
    }

    public <T extends GuiNode> T getChild(String name){
        for (GuiNode child : children) {
            if (child.getName().equals(name)){
                return (T) child;
            }
        }
        return null;
    }

    public <T extends GuiNode> T searchForChild(String name){
        for (GuiNode child : children) {
            if (child instanceof ContainerNode containerNode){
                GuiNode node = containerNode.searchForChild(name);
                if (node != null) {
                    return (T) node;
                }
            }
            if (child.getName().equals(name)){
                return (T) child;
            }
        }
        return null;
    }


    public List<GuiNode> getChildren() {
        return children;
    }

    public long getTicks() {
        return ticks;
    }

    public float getDeltaTicks(float partialTicks){
        return ((float) ticks)+partialTicks;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
