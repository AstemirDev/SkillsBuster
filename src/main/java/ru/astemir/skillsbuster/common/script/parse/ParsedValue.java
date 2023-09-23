package ru.astemir.skillsbuster.common.script.parse;

public class ParsedValue<T> {

    private T value;
    private boolean success = true;

    public ParsedValue(T value) {
        this.value = value;
    }

    public ParsedValue(boolean success) {
        this.success = success;
    }

    public static <T> ParsedValue<T> of(T value){
        return new ParsedValue<>(value);
    }

    public static <T> ParsedValue<T> fail(){
        return new ParsedValue<>(false);
    }

    public T getValueOr(T or){
        if (isSuccessfullyParsed()){
            return value;
        }else{
            return or;
        }
    }

    public T getValue() {
        return value;
    }
    public boolean isSuccessfullyParsed() {
        return success;
    }

    @Override
    public String toString() {
        if (isSuccessfullyParsed()){
            return value.toString();
        }else{
            return "failedParse";
        }
    }
}
