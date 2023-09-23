package ru.astemir.skillsbuster.common.script;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import org.astemir.api.network.PacketArgument;
import ru.astemir.skillsbuster.common.SBServerEvents;
import ru.astemir.skillsbuster.common.script.argument.ParsedScriptArgs;
import ru.astemir.skillsbuster.common.script.execute.ExecuteContext;
import ru.astemir.skillsbuster.common.script.execute.ExecutorList;
import ru.astemir.skillsbuster.common.script.execute.ScriptExecuteResult;
import ru.astemir.skillsbuster.common.script.parse.ParsedValue;
import ru.astemir.skillsbuster.common.script.parse.ScriptParser;
import ru.astemir.skillsbuster.common.script.parse.ScriptToken;
import ru.astemir.skillsbuster.manager.timer.ForgeRunnable;
import ru.astemir.skillsbuster.common.utils.SafeUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScriptNetHandler{

    public static ScriptExecuteResult executeScript(Level level,Entity entity,String script){
        ScriptParser parser = new ScriptParser().parse(script, ScriptToken.Type.SPACE);
        ParsedValue<String> strArg = parser.readArgument();
        if (strArg.isSuccessfullyParsed()) {
            ScriptType scriptType = ScriptType.byKeyword(strArg.getValue());
            ExecutorList scriptExecutor = ExecutorList.parse(parser, (ServerLevel) level, entity);
            ParsedScriptArgs parsedArgs = new ParsedScriptArgs(scriptType.getArgsList(), parser);
            ExecuteContext context = new ExecuteContext(scriptExecutor, (ServerLevel) level, parsedArgs.getValue("position",Vector3.from(entity.position())), parsedArgs.getValue("rotation",new Vector2(entity.getXRot(), entity.getYRot())));
            return scriptType.getExecutable().onExecute(parsedArgs, context);
        }else{
            return ScriptExecuteResult.NO_RESULT;
        }
    }

    public static void executeScripts(Level level,Entity entity,List<String> scripts){
        SafeUtils.runSafe(()->{
            Iterator<String> iterator = scripts.iterator();
            int nextIndex = 0;
            while(iterator.hasNext()){
                nextIndex++;
                String script = iterator.next();
                ScriptExecuteResult executeResult = executeScript(level,entity,script);
                if (executeResult instanceof ScriptExecuteResult.Delay executeDelay){
                    int finalIndex = nextIndex;
                    new ForgeRunnable(){
                        @Override
                        public void run() {
                            executeScripts(level,entity,scripts.subList(finalIndex,scripts.size()));
                        }
                    }.runTaskLater(executeDelay.getTicks());
                    break;
                }
            }
        });
    }

    public static void sendScriptsToServer(Level level,BlockPos pos,List<String> scripts){
        sendScriptsToServer(level,null,pos,scripts);
    }

    public static void sendScriptsToServer(Level level,Entity entity,BlockPos pos,List<String> scripts){
        List<PacketArgument> args = new ArrayList<>();
        args.add(PacketArgument.integer(entity != null ? entity.getId() : -1));
        for (String script : scripts) {
            args.add(PacketArgument.str(script));
        }
        SBServerEvents.runScript(level,pos,args.toArray(new PacketArgument[args.size()]));
    }
}
