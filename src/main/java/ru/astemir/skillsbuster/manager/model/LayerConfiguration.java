package ru.astemir.skillsbuster.manager.model;

import org.astemir.api.math.components.Color;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.render.texture.ModelTexture;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import ru.astemir.skillsbuster.manager.actor.EntityActor;
import ru.astemir.skillsbuster.manager.shader.ShaderRenderFunction;

public class LayerConfiguration implements PropertyHolder {

    @LoadProperty("texture")
    public ModelTexture<Object> texture;
    @LoadProperty("render-type")
    public ModelRenderType actorRenderType = ModelRenderType.DEFAULT;
    @LoadProperty("color")
    public Color color = new Color(1,1,1,1);
    @LoadProperty("shader")
    public ShaderRenderFunction shader = ShaderRenderFunction.NONE;
    @LoadProperty("size")
    public Vector3 size = new Vector3(1,1,1);
    @LoadProperty("translation")
    public Vector3 translation = new Vector3(0,0,0);
    @LoadProperty("render-self")
    public boolean renderSelf = true;
    @LoadProperty("light")
    public int light = -1;
}
