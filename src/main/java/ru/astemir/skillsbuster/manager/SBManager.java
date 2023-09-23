package ru.astemir.skillsbuster.manager;

import net.minecraft.world.level.Level;
import ru.astemir.skillsbuster.common.utils.SafeUtils;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfigManager;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface SBManager<T>{
    void onLoad(Level level);
    List<T> entries();

    default void load(Level level) {
        if (isClearOnLoad()) {
            clearEntries();
        }
        SafeUtils.runSafe(()->onLoad(level));
    }

    default T add(T object){
        entries().add(object);
        return object;
    }

    default T add(String name, T object){
        if (!containsEntry(name)) {
            entries().add(object);
            if (object instanceof NamedEntry namedEntry) {
                namedEntry.setName(name);
            }
        }
        return object;
    }

    default T get(String name){
        if (name == null){
            return null;
        }
        for (T entry : entries()) {
            if (entry instanceof NamedEntry namedEntry) {
                if (namedEntry.getName().equals(name)) {
                    return entry;
                }
            }
        }
        return null;
    }

    default void removeByName(String name){
        if (containsEntry(name)) {
            entries().remove(get(name));
        }
    }

    default void remove(T object){
        entries().remove(object);
    }

    default boolean containsEntry(String name){
        return get(name) != null;
    }

    default List<String> entriesNames(){
        List<String> names = new ArrayList<>();
        for (T entry : entries()) {
            if (entry instanceof NamedEntry namedEntry) {
                names.add(namedEntry.getName());
            }
        }
        return names;
    }

    default boolean isClearOnLoad(){return true;}

    default void clearEntries(){
        entries().clear();
    }

    abstract class Default<T> implements SBManager<T>{

        private CopyOnWriteArrayList<T> entries = new CopyOnWriteArrayList<>();
        @Override
        public CopyOnWriteArrayList<T> entries() {
            return entries;
        }
    }

    abstract class Configurable<T> extends Default<T>{
        private ConfigType type;
        public Configurable(ConfigType type) {
            this.type = type;
        }
        @Override
        public void load(Level level) {
            if (isClearOnLoad()) {
                clearEntries();
            }
            onLoadConfiguration(SBConfigManager.getInstance().getConfigurationList(type));
            SafeUtils.runSafe(()->onLoad(level));
        }

        @Override
        public void onLoad(Level level) {}
        protected abstract void onLoadConfiguration(List<SBConfig> configurations);

        public ConfigType getType() {
            return type;
        }
    }

}
