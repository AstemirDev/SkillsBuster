package ru.astemir.skillsbuster.common.script.argument;


import java.util.LinkedList;
import java.util.List;

public class ScriptArgsList {
    private List<ScriptArgument>  args = new LinkedList<>();

    public ScriptArgsList(ScriptArgument... arguments) {
        args.add(ScriptArgument.argVec3("position"));
        args.add(ScriptArgument.argVec2("rotation"));
        for (ScriptArgument argument : arguments) {
            args.add(argument);
        }
    }

    public static ScriptArgsList create(ScriptArgument... arguments){
        return new ScriptArgsList(arguments);
    }

    public ScriptArgument getArgument(String name){
        for (ScriptArgument arg : args) {
            if (arg.getName().equals(name)){
                return arg;
            }
        }
        return null;
    }

    public List<ScriptArgument> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "ScriptArgsList{" +
                "args=" + args +
                '}';
    }
}

