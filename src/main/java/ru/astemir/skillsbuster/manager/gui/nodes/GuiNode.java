package ru.astemir.skillsbuster.manager.gui.nodes;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;
import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Rect2;
import org.astemir.api.math.components.Vector2;
import org.lwjgl.glfw.GLFW;
import ru.astemir.skillsbuster.common.script.ScriptHolder;
import ru.astemir.skillsbuster.manager.gui.SBGui;
import ru.astemir.skillsbuster.client.misc.ScissorStack;
import ru.astemir.skillsbuster.client.misc.Transform2D;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;

public abstract class GuiNode implements PropertyHolder {
    @LoadProperty("transform")
    private Transform2D transform2D = new Transform2D();
    @LoadProperty("color")
    private Color color = Color.WHITE;
    @LoadProperty("visible")
    private boolean visible = true;
    @LoadProperty("scripts")
    private ScriptHolder scriptHolder = new ScriptHolder();
    @LoadProperty("local-position")
    private Vector2 localPosition = new Vector2(0,0);
    @LoadProperty("font")
    private Font font;
    @LoadProperty("name")
    private String name = "";
    private SBGui gui;
    private GuiNode parent;
    private boolean hovered = false;

    public void init(){
        runScript("on-init");
    }
    @Override
    public void onLoad(JsonObject jsonObject) {
        resize(getSize());
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks){}

    public void resize(Vector2 newSize){
        getTransform2D().setSize(newSize);
    }

    public void runScript(String scriptName){
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null) {
            scriptHolder.runScript(minecraft.level,player,scriptName);
        }
    }

    public void setMousePosition(float x,float y){
        Window window = getWindow();
        double d0 = x * (double)window.getScreenWidth() / (double)window.getGuiScaledWidth();
        double d1 = y * (double)window.getScreenHeight() / (double)window.getGuiScaledHeight();
        GLFW.glfwSetCursorPos(window.getWindow(), d0,d1);
    }
    public void renderCutOutside(Runnable runnable){
        renderCutOutside(runnable,new Rect2(0,0,0,0));
    }
    public void renderCutOutside(Runnable runnable,Rect2 extend){
        Rect2 rect = getScaledRectangle();
        ScissorStack.pushScissor(getWindow(), (int) (rect.getX()+extend.getX()), (int) (rect.getY()+extend.getY()), (int) (rect.getWidth()+extend.getWidth()),(int) (rect.getHeight()+extend.getHeight()));
        runnable.run();
        ScissorStack.popScissor(getWindow());
    }

    public void setSize(Vector2 size){
        transform2D.setSize(size);
    }

    public void setPosition(Vector2 position){
        localPosition = position;
    }
    public void setRotationDegrees(int rotationDegrees){
        transform2D.setRotationDegrees(rotationDegrees);
    }

    public void checkHoverState(double mouseX,double mouseY){
        boolean hovered = isHovered(mouseX,mouseY);
        if (hovered){
            if (onMouseHovered(mouseX,mouseY)) {
                if (!isHovered()) {
                    if (onMouseEnter(mouseX, mouseY)) {
                        setHovered(true);
                    }
                }
            }
        }
        if (!hovered) {
            if (onMouseLeave(mouseX, mouseY)) {
                if (isHovered()) {
                    setHovered(false);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setGui(SBGui gui) {
        this.gui = gui;
    }
    public void setParent(GuiNode parent) {
        this.parent = parent;
    }
    public Vector2 getLocalPosition(){
        Vector2 position = getTransform2D().getPosition();
        if (getParent() != null) {
            position = position.add(getParent().getGlobalPosition());
            if (getParent() instanceof ContainerNode container) {
                position = position.add(container.getChildrenOffset());
            }
        }
        return position;
    }
    public Vector2 relativeMousePosition(int mouseX,int mouseY){
        return new Vector2(mouseX-getLocalPosition().x,mouseY-getLocalPosition().y);
    }

    public static GuiNode fromType(String type){
        switch (type){
            case "window": return new WindowNode();
            case "button": return new ButtonNode();
        }
        return null;
    }

    public Font getFont() {
        if (font == null){
            return gui.getFont();
        }
        return font;
    }
    public boolean isHovered(double x,double y){return getScaledRectangle().contains((float) x, (float) y);}
    public Vector2 getGlobalPosition(){
        return localPosition.add(getLocalPosition());
    }

    public Vector2 getPosition(){
        return localPosition;
    }

    public Vector2 getSize(){
        return transform2D.getSize();
    }

    public Vector2 getScale(){
        return getTransform2D().getScale();
    }
    public int getRotationDegrees(){
        return getTransform2D().getRotationDegrees();
    }
    public Rect2 getScaledRectangle(){return new Rect2(getGlobalPosition(),getScaledSize());}
    public Vector2 getScaledSize(){
        return getSize().mul(getScale());
    }

    public Color getColor() {return color;}
    public Transform2D getTransform2D() {
        return transform2D;
    }

    public SBGui getGui() {
        return gui;
    }
    public GuiNode getParent() {
        return parent;
    }
    public Window getWindow() {
        return Minecraft.getInstance().getWindow();
    }
    public long getWindowId(){
        return getWindow().getWindow();
    }
    public boolean isVisible() {
        return visible;
    }
    public boolean isHovered() {
        return hovered;
    }
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        runScript("on-mouse-clicked");
        return true;
    }
    public boolean onMouseReleased(double mouseX, double pMouseY, int button) {
        runScript("on-mouse-release");
        return true;
    }
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        runScript("on-mouse-drag");
        return true;
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
        runScript("on-mouse-scroll");
        return true;
    }

    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        runScript("on-key-release");
        return true;
    }
    public boolean onCharTyped(char codePoint, int modifiers) {
        runScript("on-char-type");
        return true;
    }

    public boolean onMouseHovered(double mouseX,double mouseY){
        runScript("on-mouse-hover");
        return true;
    }

    public boolean onMouseEnter(double mouseX,double mouseY){
        runScript("on-mouse-enter");
        return true;
    }
    public boolean onMouseLeave(double mouseX,double mouseY){
        runScript("on-mouse-leave");
        return true;
    }
}
