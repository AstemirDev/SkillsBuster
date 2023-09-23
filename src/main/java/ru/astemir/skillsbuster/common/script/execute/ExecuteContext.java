package ru.astemir.skillsbuster.common.script.execute;

import net.minecraft.server.level.ServerLevel;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;

public class ExecuteContext {
    private ExecutorList executor;
    private ServerLevel level;
    private Vector3 position;
    private Vector2 rotation;

    public ExecuteContext(ExecutorList executor, ServerLevel level, Vector3 position, Vector2 rotation) {
        this.executor = executor;
        this.level = level;
        this.position = position;
        this.rotation = rotation;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector2 getRotation() {
        return rotation;
    }


    public ExecutorList getExecutorList() {
        return executor;
    }
}
