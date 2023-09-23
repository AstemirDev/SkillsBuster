package ru.astemir.skillsbuster.common.script.execute;

public interface ScriptExecuteResult {
    ScriptExecuteResult NO_RESULT = new ScriptExecuteResult(){};
    static ReturnValue returnValue(Object value){
        return new ReturnValue(value);
    }

    static Delay delay(int ticks){
        return new Delay(ticks);
    }

    class ReturnValue implements ScriptExecuteResult{

        private Object value;
        public ReturnValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }

    class Delay implements ScriptExecuteResult{
        private int ticks;
        public Delay(int ticks) {
            this.ticks = ticks;
        }
        public int getTicks() {
            return ticks;
        }
    }
}
