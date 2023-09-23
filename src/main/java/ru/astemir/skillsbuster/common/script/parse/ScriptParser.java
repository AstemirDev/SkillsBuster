package ru.astemir.skillsbuster.common.script.parse;

import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.common.misc.TextScanner;
import ru.astemir.skillsbuster.common.utils.ReflectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class ScriptParser {
    private List<ScriptToken> tokens = new ArrayList<>();
    private int index = 0;
    public ScriptParser parse(String line, ScriptToken.Type... skipped){
        TextScanner scanner = new TextScanner(line);
        while(scanner.hasNext()){
            ScriptToken token = tryParse(scanner);
            boolean skip = false;
            for (ScriptToken.Type tokenType : skipped) {
                if (tokenType == token.getType()){
                    skip = true;
                    break;
                }
            }
            if (!skip){
                tokens.add(token);
            }
        }
        return this;
    }
    public ParsedValue<String> readString(){
        if (hasNext() && isAt(ScriptToken.Type.TEXT)) {
            return ParsedValue.of(consume(ScriptToken.Type.TEXT).getAsString());
        }else{
            return ParsedValue.fail();
        }
    }

    public ParsedValue<String> readArgument(){
        if (hasNext() && isAt(ScriptToken.Type.ARGUMENT)) {
            return ParsedValue.of(consume(ScriptToken.Type.ARGUMENT).getAsString());
        }else{
            return ParsedValue.fail();
        }
    }

    public ParsedValue<Integer> readInt(){
        if (hasNext() && isAt(ScriptToken.Type.NUMBER)) {
            return ParsedValue.of(consume(ScriptToken.Type.NUMBER).getAsInt());
        }else{
            return ParsedValue.fail();
        }
    }
    public ParsedValue<Double> readDouble(){
        if (hasNext() && isAt(ScriptToken.Type.NUMBER)) {
            return ParsedValue.of(consume(ScriptToken.Type.NUMBER).getAsDouble());
        }else{
            return ParsedValue.fail();
        }
    }

    public ParsedValue<Float> readFloat(){
        if (hasNext() && isAt(ScriptToken.Type.NUMBER)) {
            return ParsedValue.of(consume(ScriptToken.Type.NUMBER).getAsFloat());
        }else{
            return ParsedValue.fail();
        }
    }

    public ParsedValue<Boolean> readBoolean(){
        if (hasNext() && isAt(ScriptToken.Type.BOOLEAN)) {
            return ParsedValue.of(consume(ScriptToken.Type.BOOLEAN).getAsBoolean());
        }else{
            return ParsedValue.fail();
        }
    }

    public <T extends Enum<?>> ParsedValue<T> readEnum(Class<T> enumClass){
        if (hasNext() && isAt(ScriptToken.Type.ARGUMENT)) {
            try {
                ParsedValue<String> parsedValue = readArgument();
                if (parsedValue.isSuccessfullyParsed()){
                    T res = ReflectionUtils.searchEnum(enumClass,parsedValue.getValue());
                    if (res != null) {
                        return ParsedValue.of(res);
                    }
                }else{
                    return ParsedValue.fail();
                }
            }catch (Exception e){
                return ParsedValue.fail();
            }
        }
        return ParsedValue.fail();
    }

    public ParsedValue<Vector2> readVec2(){
        ParsedValue<List<ParsedValue<Float>>> value = readListFloat(2);
        if (value.isSuccessfullyParsed()) {
            List<ParsedValue<Float>> floats = value.getValue();
            ParsedValue<Float> x = floats.get(0);
            ParsedValue<Float> y = floats.get(1);
            if (x.isSuccessfullyParsed() && y.isSuccessfullyParsed()){
                return ParsedValue.of(new Vector2(x.getValue(),y.getValue()));
            }
        }
        return ParsedValue.fail();
    }

    public ParsedValue<Vector3> readVec3(){
        ParsedValue<List<ParsedValue<Float>>> value = readListFloat(3);
        if (value.isSuccessfullyParsed()) {
            List<ParsedValue<Float>> floats = value.getValue();
            if (floats.size() == 3) {
                ParsedValue<Float> x = floats.get(0);
                ParsedValue<Float> y = floats.get(1);
                ParsedValue<Float> z = floats.get(2);
                if (x.isSuccessfullyParsed() && y.isSuccessfullyParsed() && z.isSuccessfullyParsed()) {
                    return ParsedValue.of(new Vector3(x.getValue(), y.getValue(), z.getValue()));
                }
            }
        }
        return ParsedValue.fail();
    }

    public ParsedValue<List<ParsedValue<Integer>>> readListInt(int count){
        return readList(()->readInt(),count);
    }

    public ParsedValue<List<ParsedValue<Double>>> readListDouble(int count){
        return readList(()->readDouble(),count);
    }

    public ParsedValue<List<ParsedValue<Float>>> readListFloat(int count){
        return readList(()->readFloat(),count);
    }
    public ParsedValue<List<ParsedValue<String>>> readListString(int count){
        return readList(()->readString(),count);
    }
    public ParsedValue<List<ParsedValue<String>>> readListArgument(int count){
        return readList(()->readArgument(),count);
    }

    public <T extends ParsedValue> ParsedValue<List<T>> readList(Callable<T> readFunc,int count){
        int i = 0;
        int beginIndex = index;
        List<T> list = new ArrayList<>();
        if (!hasNext() || !isAt(ScriptToken.Type.OPEN_BRACKET)) {
            return ParsedValue.fail();
        }
        consume(ScriptToken.Type.OPEN_BRACKET);
        while (hasNext() && !isAt(ScriptToken.Type.CLOSE_BRACKET)) {
            if (canPeek(1) && peek(1).is(ScriptToken.Type.COMMA)) {
                try {
                    list.add(readFunc.call());
                    i++;
                } catch (Exception e) {
                    return ParsedValue.fail();
                }
                consume(ScriptToken.Type.COMMA);
            } else {
                try {
                    list.add(readFunc.call());
                    i++;
                } catch (Exception e) {
                    return ParsedValue.fail();
                }
                break;
            }
        }
        consume(ScriptToken.Type.CLOSE_BRACKET);
        if (count == i){
            return ParsedValue.of(list);
        }else{
            setIndex(beginIndex);
            return ParsedValue.fail();
        }
    }

    public ScriptToken next() {
        ScriptToken token = tokens.get(index);
        index++;
        return token;
    }

    public ScriptToken consume(ScriptToken.Type type){
        if (isAt(type)){
            return next();
        }
        throw new RuntimeException("Invalid token "+current()+" should be "+type);
    }
    public boolean isAt(ScriptToken.Type type){
        return tokens.get(index).getType() == type;
    }

    public ScriptToken peek(int i){
        return tokens.get(index+i);
    }

    public ScriptToken current(){
        return tokens.get(index);
    }

    public boolean hasNext(){
        return index < tokens.size();
    }

    public boolean canPeek(int i){
        return index+i<tokens.size();
    }

    public void addToken(ScriptToken token){
        tokens.add(token);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public List<ScriptToken> getTokens() {
        return tokens;
    }

    private static ScriptToken tryParse(TextScanner scanner){
        for (ScriptToken.Type tokenType : ScriptToken.Type.values()) {
            if (tokenType.getPredictor().canConstruct(scanner)){
                return new ScriptToken(tokenType,tokenType.getPredictor().construct(scanner));
            }
        }
        throw new RuntimeException("Invalid token type");
    }

    @Override
    public String toString() {
        return "ScriptParser{" +
                "tokens=" + tokens +
                ", index=" + index +
                '}';
    }
}
