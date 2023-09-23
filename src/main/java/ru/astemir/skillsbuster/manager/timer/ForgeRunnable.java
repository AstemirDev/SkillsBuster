package ru.astemir.skillsbuster.manager.timer;

public abstract class ForgeRunnable {
    private long lifeTime = 0;
    public ForgeRunnable runTaskLater(long delay){
        return ForgeRunnableHandler.getInstance().add(create(delay));
    }

    public ForgeRunnable create(long delay) {
        this.lifeTime = ForgeRunnableHandler.getGlobalTicks()+delay;
        return this;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public abstract void run();
}
