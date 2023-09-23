package ru.astemir.skillsbuster.manager.actor;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.astemir.api.common.commands.build.CommandArgument;
import org.astemir.api.common.commands.build.CommandBuilder;
import org.astemir.api.common.commands.build.CommandPart;
import org.astemir.api.common.commands.build.CommandVariant;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.client.SBClientEvents;
import ru.astemir.skillsbuster.common.io.json.PropertyHolder;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.config.SBConfig;

import java.util.LinkedList;
import java.util.List;

public class SBActorManager extends SBManager.Configurable<ActorConfiguration> {

    public static final SuggestionProvider<CommandSourceStack> ACTORS = SuggestionProviders.register(new ResourceLocation(SkillsBuster.MODID,"actors"), (context, builder)-> SharedSuggestionProvider.suggest(SBActorManager.getInstance().entriesNames(), builder));

    public static final SuggestionProvider<CommandSourceStack> ENTITY_ACTORS = SuggestionProviders.register(new ResourceLocation(SkillsBuster.MODID,"entity_actors"), (context, builder)->{
        List<String> list = new LinkedList<>();
        for (EntityActor actor : EntityActor.ACTORS) {
            list.add(actor.getStringUUID());
        }
        return SharedSuggestionProvider.suggest(list, builder);
    });

    public static final SuggestionProvider<CommandSourceStack> SCRIPTS = SuggestionProviders.register(new ResourceLocation(SkillsBuster.MODID,"scripts"), (context, builder)->{
        return SharedSuggestionProvider.suggest(new String[]{}, builder);
    });

    private static SBActorManager instance;


    public SBActorManager() {
        super(ConfigType.ACTORS);
        instance = this;
    }

    @Override
    public void onLoadConfiguration(List<SBConfig> configurations){
        for (SBConfig configuration : configurations) {
            JsonObject keybindingsJson = configuration.getFile().getJsonObject("actors");
            for (String name : keybindingsJson.keySet()) {
                ActorConfiguration actorConfig = PropertyHolder.buildHolder(ActorConfiguration.class,keybindingsJson.get(name));
                actorConfig.setName(name);
                add(name,actorConfig);
            }
        }
    }

    @Override
    public void onLoad(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getAllEntities().forEach((entity) -> {
                if (entity instanceof EntityActor actor) {
                    String configName = actor.getConfiguration().getName();
                    ActorConfiguration configuration = get(configName);
                    actor.loadConfiguration(configuration);
                    SBClientEvents.setActorConfiguration(level, actor, configName);
                }
            });
        }
    }

    public static void commandActor(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandBuilder command = new CommandBuilder("actor");
        CommandArgument posArg = CommandArgument.blockPos("pos");
        CommandArgument actorArg = CommandArgument.string("actor").suggestion(ACTORS);
        CommandArgument entityArg = CommandArgument.entities("entity").suggestion(ENTITY_ACTORS);
        CommandArgument scriptArg = CommandArgument.string("script").suggestion(SCRIPTS);
        command.variants(
                new CommandVariant(CommandPart.create("spawn"),posArg,actorArg).execute((p)->{
                    Level level = p.getSource().getLevel();
                    ActorConfiguration configuration = SBActorManager.getInstance().get(actorArg.getString(p));
                    configuration.spawn(level, Vector3.from(posArg.getBlockPos(p)));
                    return 1;
                }),
                new CommandVariant(CommandPart.create("script"),entityArg,scriptArg).execute((p)->{
                    Level level = p.getSource().getLevel();
                    for (Entity entity : entityArg.getEntities(p)) {
                        if (entity instanceof EntityActor actor){
                            actor.getConfiguration().scriptHolder.runScript(level,entity,scriptArg.getString(p));
                        }
                    }
                    return 1;
                }),
                new CommandVariant(CommandPart.create("morph"),actorArg).execute((p)->{
                    Level level = p.getSource().getLevel();
                    Entity entity = p.getSource().getEntity();
                    ActorConfiguration configuration = SBActorManager.getInstance().get(actorArg.getString(p));
                    configuration.spawn(level, Vector3.from(entity.getPosition(0)));
                    entity.setInvisible(true);
                    entity.getPersistentData().putBoolean("IsMorphed",true);
                    return 1;
                }),
                new CommandVariant(CommandPart.create("unmorph"),actorArg).execute((p)->{
                    Entity entity = p.getSource().getEntity();
                    entity.getPersistentData().putBoolean("IsMorphed",false);
                    entity.setInvisible(false);
                    return 1;
                })
        );
        dispatcher.register(command.permission((p)->p.hasPermission(2)).build());
    }

    public static SBActorManager getInstance() {
        return instance;
    }
}
