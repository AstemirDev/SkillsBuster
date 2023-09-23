package ru.astemir.skillsbuster.common.script.argument;

import ru.astemir.skillsbuster.common.script.parse.ParsedValue;
import ru.astemir.skillsbuster.common.script.parse.ScriptParser;
import java.util.HashMap;
import java.util.Map;

public class ParsedScriptArgs {
    private Map<String, ParsedValue> values = new HashMap<>();
    private ScriptParser parser;

    public ParsedScriptArgs(ScriptArgsList args, ScriptParser parser) {
        this.parser = parser;
        for (ScriptArgument arg : args.getArgs()) {
            values.put(arg.getName(),arg.read(parser));
        }
    }

    public boolean hasValue(String argName){
        return values.containsKey(argName);
    }

    public <T> T getValue(String argName){
        if (values.containsKey(argName)){
            ParsedValue parsedValue = values.get(argName);
            return (T) parsedValue.getValueOr(null);
        }
        return null;
    }

    public <T> T getValue(String argName,T defaultValue){
        if (values.containsKey(argName)){
            ParsedValue parsedValue = values.get(argName);
            return (T) parsedValue.getValueOr(defaultValue);
        }
        return defaultValue;
    }

    public ScriptParser getParser() {
        return parser;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
