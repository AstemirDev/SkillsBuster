package ru.astemir.skillsbuster.manager.gui.nodes;

import com.mojang.blaze3d.vertex.PoseStack;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerNode extends GuiNode implements ChildHolder{
    @LoadProperty("children")
    private List<GuiNode> children = new ArrayList<>();

    @Override
    public void init() {
        super.init();
        for (GuiNode child : children) {
            child.setGui(getGui());
            child.setParent(this);
        }
    }

    public void renderChildren(PoseStack stack, int mouseX, int mouseY, float partialTicks){
        for (GuiNode child : children) {
            child.checkHoverState(mouseX,mouseY);
            if (child.isVisible()) {
                child.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public boolean onMouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (GuiNode child : children) {
            child.onMouseClicked(pMouseX,pMouseY,pButton);
        }
        return super.onMouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean onMouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (GuiNode child : children) {
            child.onMouseReleased(pMouseX, pMouseY, pButton);
        }
        return super.onMouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean onMouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for (GuiNode child : children) {
            child.onMouseDragged(pMouseX,pMouseY,pButton,pDragX,pDragY);
        }
        return super.onMouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean onMouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (GuiNode child : children) {
            child.onMouseScrolled(pMouseX,pMouseY,pDelta);
        }
        return super.onMouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean onKeyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (GuiNode child : children) {
            child.onKeyReleased(pKeyCode, pScanCode, pModifiers);
        }
        return super.onKeyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean onCharTyped(char pCodePoint, int pModifiers) {
        for (GuiNode child : children) {
            child.onCharTyped(pCodePoint, pModifiers);
        }
        return super.onCharTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean onMouseHovered(double mouseX, double mouseY) {
        for (GuiNode child : children) {
            child.onMouseHovered(mouseX,mouseY);
        }
        return super.onMouseHovered(mouseX, mouseY);
    }

    @Override
    public boolean onMouseEnter(double mouseX, double mouseY) {
        for (GuiNode child : children) {
            child.onMouseEnter(mouseX,mouseY);
        }
        return super.onMouseEnter(mouseX, mouseY);
    }

    @Override
    public boolean onMouseLeave(double mouseX, double mouseY) {
        for (GuiNode child : children) {
            child.onMouseLeave(mouseX,mouseY);
        }
        return super.onMouseLeave(mouseX, mouseY);
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

    @Override
    public List<GuiNode> getChildren() {
        return children;
    }
}
