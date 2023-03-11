package dev.zanckor.mod;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.zanckor.mod.common.datapack.DialogJSONListener;
import dev.zanckor.mod.common.datapack.QuestJSONListener;
import dev.zanckor.mod.server.command.QuestCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.zanckor.mod.QuestApiMain.serverQuests;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID)
public class EventHandlerRegister {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("quests")
                .requires((player) -> player.hasPermission(3))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("questID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::addQuestSuggestions)
                                        .executes((context) -> {
                                            try {
                                                return QuestCommand.addQuest(
                                                        context,
                                                        EntityArgument.getPlayer(context, "player").getUUID(),
                                                        StringArgumentType.getString(context, "questID"));
                                            } catch (IOException ex) {
                                                QuestApiMain.LOGGER.error(ex.getMessage());

                                                return 0;
                                            }
                                        }))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("questID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::removeQuestSuggestions)
                                        .executes((context) -> {
                                            try {
                                                return QuestCommand.removeQuest(
                                                        context,
                                                        EntityArgument.getPlayer(context, "player").getUUID(),
                                                        StringArgumentType.getString(context, "questID"));
                                            } catch (IOException ex) {
                                                QuestApiMain.LOGGER.error(ex.getMessage());

                                                return 0;
                                            }
                                        }))))

                .then(net.minecraft.commands.Commands.literal("tracked")
                        .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                .then(net.minecraft.commands.Commands.argument("questID", StringArgumentType.string())
                                        .suggests(EventHandlerRegister::trackedQuestSuggestions)
                                        .executes((context) -> {
                                            try {
                                                return QuestCommand.trackedQuest(
                                                        context,
                                                        EntityArgument.getPlayer(context, "player").getUUID(),
                                                        StringArgumentType.getString(context, "questID"));
                                            } catch (IOException ex) {
                                                QuestApiMain.LOGGER.error(ex.getMessage());

                                                return 0;
                                            }
                                        }))))

                .then(net.minecraft.commands.Commands.literal("reload")
                        .then(net.minecraft.commands.Commands.argument("identifier", StringArgumentType.string())
                                .executes((context) -> {
                                    return QuestCommand.reloadQuests(
                                            context,
                                            StringArgumentType.getString(context, "identifier"));
                                }))));


        ConfigCommand.register(e.getDispatcher());
    }


    private static CompletableFuture<Suggestions> removeQuestSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        Player player = ctx.getSource().getPlayer();
        List<File[]> questsFile = new ArrayList<>();

        Path userFolder = QuestApiMain.getUserFolder(player.getUUID());

        Path activeQuestFolder = QuestApiMain.getActiveQuest(userFolder);
        Path completedQuestFolder = QuestApiMain.getCompletedQuest(userFolder);
        Path uncompletedQuestFolder = QuestApiMain.getUncompletedQuest(userFolder);


        questsFile.add(activeQuestFolder.toFile().listFiles());
        questsFile.add(completedQuestFolder.toFile().listFiles());
        questsFile.add(uncompletedQuestFolder.toFile().listFiles());

        for (File[] questList : questsFile) {
            for (File quest : questList) {
                builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
            }
        }

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> trackedQuestSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        Player player = ctx.getSource().getPlayer();
        Path userFolder = QuestApiMain.getUserFolder(player.getUUID());

        File[] questList = QuestApiMain.getActiveQuest(userFolder).toFile().listFiles();

        for (File quest : questList) {
            builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
        }

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> addQuestSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        Player player = ctx.getSource().getPlayer();

        File[] questsFile = serverQuests.toFile().listFiles();

        for (File quest : questsFile) {
            builder.suggest(quest.getName().substring(0, quest.getName().length() - 5));
        }

        return builder.buildFuture();
    }

    @SubscribeEvent
    public static void jsonListener(AddReloadListenerEvent e){
        QuestJSONListener.register(e);
        DialogJSONListener.register(e);
    }
}
