package ru.astemir.skillsbuster.manager.model;

import org.astemir.api.client.animation.InterpolationType;
import org.astemir.api.client.animation.SmoothnessType;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.math.components.Color;
import ru.astemir.skillsbuster.client.render.texture.ModelTexture;
import ru.astemir.skillsbuster.common.io.json.LoadProperty;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import ru.astemir.skillsbuster.manager.NamedEntry;
import ru.astemir.skillsbuster.manager.shader.ShaderRenderFunction;
import java.util.ArrayList;
import java.util.List;

public class ModelConfiguration implements PropertyHolder, NamedEntry {

    @LoadProperty("model")
    public String model = "";

    @LoadProperty("animation")
    public String animation = "";

    @LoadProperty("texture")
    public ModelTexture<Object> texture;
    @LoadProperty("block-light")
    public int blockLight = -1;
    @LoadProperty("light")
    public int light = -1;
    @LoadProperty("interpolation")
    public InterpolationType interpolationType = InterpolationType.CATMULLROM;
    @LoadProperty("smoothness-type")
    public SmoothnessType smoothnessType = SmoothnessType.SQR_EXPONENTIAL;
    @LoadProperty("smoothness")
    public float smoothness = 2;
    @LoadProperty("player-bone-render")
    public boolean playerBoneRender = true;

    @LoadProperty("color")
    public Color color = new Color(1,1,1,1);
    @LoadProperty("render-type")
    public ModelRenderType actorRenderType = ModelRenderType.DEFAULT;
    @LoadProperty("shader")
    public ShaderRenderFunction shader = ShaderRenderFunction.NONE;
    @LoadProperty("render-self")
    public boolean renderSelf = true;
    @LoadProperty("layers")
    public List<LayerConfiguration> layers = new ArrayList<>();



    public SkillsModel baked;
    private String name;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
