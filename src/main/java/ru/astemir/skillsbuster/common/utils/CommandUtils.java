package ru.astemir.skillsbuster.common.utils;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;

public class CommandUtils {

    public static void runCommand(ServerLevel level, Vector3 position, Vector2 rotation, String command,Entity entity){
        String text = entity == null ? "Script" : entity.getName().getString();
        Component component = entity == null ? Component.literal("Script") : entity.getDisplayName();
        CommandSourceStack sourceStack = new CommandSourceStack(CommandSource.NULL, position.toVec3(), new Vec2(rotation.x,rotation.y),level, 2, text, component, level.getServer(), entity);
        level.getServer().getCommands().performPrefixedCommand(sourceStack,command);
    }

    public static void runCommand(ServerLevel level, ParseResults<CommandSourceStack> parseResults, String command){
        level.getServer().getCommands().performCommand(parseResults,command);
    }
}
