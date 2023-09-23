package ru.astemir.skillsbuster.common.script.argument;

import ru.astemir.skillsbuster.common.script.parse.ParsedValue;
import ru.astemir.skillsbuster.common.script.parse.ScriptParser;

public class ScriptArgument {

    private Class<?> argumentClass;
    private ArgumentType argumentType;

    private String name;
    private int listSize;

    private ScriptArgument(String name,Class<?> argumentClass, ArgumentType argumentType, int listSize) {
        this.name = name;
        this.argumentClass = argumentClass;
        this.argumentType = argumentType;
        this.listSize = listSize;
    }

    public Class<?> getArgumentClass() {
        return argumentClass;
    }

    public String getName() {
        return name;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public int getListSize() {
        return listSize;
    }

    public static ScriptArgument argInt(String name){
        return new ScriptArgument(name,null,ArgumentType.INT,0);
    }
    public static ScriptArgument argDouble(String name){
        return new ScriptArgument(name,null,ArgumentType.DOUBLE,0);
    }
    public static ScriptArgument argFloat(String name){
        return new ScriptArgument(name,null,ArgumentType.FLOAT,0);
    }
    public static ScriptArgument argBoolean(String name){
        return new ScriptArgument(name,null,ArgumentType.BOOLEAN,0);
    }
    public static ScriptArgument argString(String name){
        return new ScriptArgument(name,null,ArgumentType.STRING,0);
    }
    public static ScriptArgument argArgument(String name){
        return new ScriptArgument(name,null,ArgumentType.ARGUMENT,0);
    }
    public static ScriptArgument argVec2(String name){
        return new ScriptArgument(name,null,ArgumentType.VEC2,0);
    }
    public static ScriptArgument argVec3(String name){
        return new ScriptArgument(name,null,ArgumentType.VEC3,0);
    }
    public static ScriptArgument argEnum(String name,Class<? extends Enum> enumClass){
        return new ScriptArgument(name,enumClass,ArgumentType.ENUM,0);
    }
    public static ScriptArgument argListArgument(String name,int size){
        return new ScriptArgument(name,null,ArgumentType.LIST_ARGUMENT,size);
    }
    public static ScriptArgument argListString(String name,int size){
        return new ScriptArgument(name,null,ArgumentType.LIST_STRING,size);
    }
    public static ScriptArgument argListFloat(String name,int size){
        return new ScriptArgument(name,null,ArgumentType.LIST_FLOAT,size);
    }
    public static ScriptArgument argListDouble(String name,int size){
        return new ScriptArgument(name,null,ArgumentType.LIST_DOUBLE,size);
    }
    public static ScriptArgument argListInt(String name,int size){
        return new ScriptArgument(name,null,ArgumentType.LIST_INT,size);
    }

    public ParsedValue read(ScriptParser parser) {
        switch (argumentType){
            case ARGUMENT: return parser.readArgument();
            case INT: return parser.readInt();
            case DOUBLE: return parser.readDouble();
            case FLOAT: return parser.readFloat();
            case STRING: return parser.readString();
            case BOOLEAN: return parser.readBoolean();
            case ENUM: return parser.readEnum((Class<? extends Enum>)argumentClass);
            case VEC2: return parser.readVec2();
            case VEC3: return parser.readVec3();
            case LIST_ARGUMENT: return parser.readListArgument(listSize);
            case LIST_DOUBLE: return parser.readListDouble(listSize);
            case LIST_FLOAT: return parser.readListFloat(listSize);
            case LIST_INT: return parser.readListInt(listSize);
            case LIST_STRING: return parser.readListString(listSize);
        }
        throw new RuntimeException("Unexpected value error while parsing in "+parser);
    }


    public enum ArgumentType {
        ARGUMENT, INT,DOUBLE,FLOAT, STRING, BOOLEAN,ENUM, VEC2, VEC3,
        LIST_ARGUMENT, LIST_DOUBLE,LIST_FLOAT, LIST_INT,LIST_STRING;

        public boolean isEnum(){
            return this == ENUM;
        }

        public boolean isList(){
            switch (this){
                case LIST_ARGUMENT,LIST_DOUBLE,LIST_FLOAT,LIST_INT,LIST_STRING -> {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
