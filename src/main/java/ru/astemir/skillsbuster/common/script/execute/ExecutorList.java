package ru.astemir.skillsbuster.common.script.execute;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import ru.astemir.skillsbuster.common.script.parse.ScriptParser;
import ru.astemir.skillsbuster.common.script.parse.ScriptToken;
import ru.astemir.skillsbuster.manager.actor.EntityActor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class ExecutorList {

    private ScriptExecutor[] executors;
    private ExecutorList(ScriptExecutor[] executors) {
        this.executors = executors;
    }

    public static ExecutorList of(Object object){
        return new ExecutorList(new ScriptExecutor[]{new ScriptExecutor(object)});
    }

    public static <T> ExecutorList list(List<T> objectList){
        ScriptExecutor[] executors = new ScriptExecutor[objectList.size()];
        for (int i = 0; i < objectList.size(); i++) {
            executors[i] = new ScriptExecutor(objectList.get(i));
        }
        return new ExecutorList(executors);
    }

    public static ExecutorList parse(ScriptParser parser, ServerLevel level, Object defaultValue){
        if (parser.hasNext()) {
            ScriptToken token = parser.current();
            if (token.is(ScriptToken.Type.ARGUMENT)) {
                String target = token.getAsString();
                switch (target) {
                    case "players":{
                        parser.consume(ScriptToken.Type.ARGUMENT);
                        return ExecutorList.list(level.getServer().getPlayerList().getPlayers());
                    }
                    case "actor":{
                        parser.consume(ScriptToken.Type.ARGUMENT);
                        parser.consume(ScriptToken.Type.COLON);
                        String name = parser.readString().getValue();
                        Iterator<Entity> iterator = level.getAllEntities().iterator();
                        List<Entity> actors = new ArrayList<>();
                        while(iterator.hasNext()){
                            Entity entity = iterator.next();
                            if (entity instanceof EntityActor actor){
                                if (actor.getConfiguration().getName().equals(name)){
                                    actors.add(entity);
                                }
                            }
                        }
                        return ExecutorList.of(actors);
                    }
                    case "player": {
                        parser.consume(ScriptToken.Type.ARGUMENT);
                        parser.consume(ScriptToken.Type.COLON);
                        String byArg = parser.readArgument().getValueOr("name");
                        switch (byArg) {
                            case "name": {
                                parser.consume(ScriptToken.Type.EQUALS);
                                String name = parser.readString().getValue();
                                return ExecutorList.of(level.getServer().getPlayerList().getPlayerByName(name));
                            }
                            case "uuid": {
                                parser.consume(ScriptToken.Type.EQUALS);
                                UUID uuid = UUID.fromString(parser.readString().getValue());
                                return ExecutorList.of(level.getServer().getPlayerList().getPlayer(uuid));
                            }
                        }
                        return ExecutorList.of(defaultValue);
                    }
                    case "entity": {
                        parser.consume(ScriptToken.Type.ARGUMENT);
                        parser.consume(ScriptToken.Type.COLON);
                        String byArg = parser.readArgument().getValueOr("name");
                        switch (byArg) {
                            case "name": {
                                parser.consume(ScriptToken.Type.EQUALS);
                                String name = parser.readString().getValue();
                                Iterator<Entity> iterator = level.getEntities().getAll().iterator();
                                while (iterator.hasNext()) {
                                    Entity entity = iterator.next();
                                    if (entity.getCustomName().getString().equals(name)) {
                                        return ExecutorList.of(entity);
                                    }
                                }
                                return ExecutorList.of(defaultValue);
                            }
                            case "uuid": {
                                parser.consume(ScriptToken.Type.EQUALS);
                                UUID uuid = UUID.fromString(parser.readString().getValue());
                                return ExecutorList.of(level.getEntity(uuid));
                            }
                        }
                        return ExecutorList.of(defaultValue);
                    }
                }
            }
        }
        return ExecutorList.of(defaultValue);
    }


    public ScriptExecutor[] getExecutors() {
        return executors;
    }
}
