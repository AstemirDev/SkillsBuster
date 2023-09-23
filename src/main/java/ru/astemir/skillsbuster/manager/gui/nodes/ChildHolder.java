package ru.astemir.skillsbuster.manager.gui.nodes;

import org.astemir.api.math.components.Vector2;

import java.util.List;
public interface ChildHolder {
    List<GuiNode> getChildren();

    default Vector2 getChildrenOffset(){return Vector2.zero();}
}
