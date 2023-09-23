package ru.astemir.skillsbuster.common.script.execute;

import net.minecraft.world.entity.Entity;

public class ScriptExecutor {
    private Object executor;
    public ScriptExecutor(Object executor) {
        this.executor = executor;
    }

    public boolean isEntity(){
        return executor instanceof Entity;
    }

    public Entity getEntity(){
        return (Entity) executor;
    }
}
