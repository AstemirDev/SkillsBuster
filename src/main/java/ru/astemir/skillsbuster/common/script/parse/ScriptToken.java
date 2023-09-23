package ru.astemir.skillsbuster.common.script.parse;

import ru.astemir.skillsbuster.common.misc.TextScanner;

public class ScriptToken {
    private Type type;
    private Object value;

    public ScriptToken(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
    public boolean is(Type type){
        return getType() == type;
    }

    public Type getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    public String getAsString(){
        return String.valueOf(value);
    }
    public int getAsInt(){
        return ((Number) value).intValue();
    }
    public double getAsDouble(){
        return ((Number) value).doubleValue();
    }
    public float getAsFloat(){
        return ((Number) value).floatValue();
    }

    public boolean getAsBoolean(){
        return (boolean) value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }

    public static ScriptToken number(Number number){
        return new ScriptToken(Type.NUMBER,number);
    }

    public enum Type {
        SPACE(TokenPredictor.predictChars(' ','\n')),
        EQUALS(TokenPredictor.predictChars('=')),
        COMMA(TokenPredictor.predictChars(',')),
        OPEN_BRACKET(TokenPredictor.predictChars('[')),
        CLOSE_BRACKET(TokenPredictor.predictChars(']')),
        COLON(TokenPredictor.predictChars(':')),
        TEXT(new TokenPredictor(){
            @Override
            public Object construct(TextScanner scanner) {
                return scanner.parseString();
            }
            @Override
            public boolean canConstruct(TextScanner scanner) {
                return scanner.isAt('\"','\'');
            }
        }),
        NUMBER(new TokenPredictor() {
            @Override
            public Object construct(TextScanner scanner) {
                return scanner.parseNumber();
            }
            @Override
            public boolean canConstruct(TextScanner scanner) {
                char current = scanner.currentChar();
                return Character.isDigit(current) || scanner.isAt('.','-','+');
            }
        }),

        BOOLEAN(new TokenPredictor() {
            @Override
            public Object construct(TextScanner scanner) {
                return scanner.parseBoolean();
            }
            @Override
            public boolean canConstruct(TextScanner scanner) {
                return scanner.isAt("true") || scanner.isAt("false");
            }
        }),

        ARGUMENT(scanner -> scanner.readUntil((character)-> hasResult(scanner)));

        private TokenPredictor predictor;
        Type(TokenPredictor predictor) {
            this.predictor = predictor;
        }

        public TokenPredictor getPredictor() {
            return predictor;
        }

        public static boolean hasResult(TextScanner scanner){
            for (Type tokenType : Type.values()) {
                if (tokenType != Type.ARGUMENT) {
                    if (tokenType.getPredictor().canConstruct(scanner)) {
                        return true;
                    }
                }else{
                    return false;
                }
            }
            return false;
        }

        public interface TokenPredictor{
            Object construct(TextScanner scanner);

            default boolean canConstruct(TextScanner scanner){
                return true;
            }

            static TokenPredictor predictChars(char... chars){
                return new TokenPredictor() {
                    @Override
                    public Object construct(TextScanner scanner) {
                        return scanner.nextChar();
                    }
                    @Override
                    public boolean canConstruct(TextScanner scanner) {
                        for (char aChar : chars) {
                            if (aChar == scanner.currentChar()){
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        }
    }
}