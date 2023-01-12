package com.zanckor.questapi;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.zanckor.questapi.commands.QuestCommands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = QuestApi.MOD_ID)
public class EventHandlerRegister {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e) {
        {
            e.getDispatcher().register(net.minecraft.commands.Commands.literal("quests")
                    .then(net.minecraft.commands.Commands.literal("add")
                            .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                    .then(net.minecraft.commands.Commands.argument("questID", IntegerArgumentType.integer())
                                            .executes((context) -> {
                                                try {
                                                    return QuestCommands.addQuest(
                                                            context,
                                                            EntityArgument.getPlayer(context, "player").getUUID(),
                                                            IntegerArgumentType.getInteger(context, "questID"));
                                                } catch (IOException ex) {
                                                    QuestApi.LOGGER.error("File reader error");

                                                    return 0;
                                                }
                                            }))))

                    .then(net.minecraft.commands.Commands.literal("remove")
                            .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                                    .then(net.minecraft.commands.Commands.argument("questID", IntegerArgumentType.integer())
                                            .executes((context) -> {
                                                try {
                                                    return QuestCommands.removeQuest(
                                                            context,
                                                            EntityArgument.getPlayer(context, "player").getUUID(),
                                                            IntegerArgumentType.getInteger(context, "questID"));
                                                } catch (IOException ex) {
                                                    QuestApi.LOGGER.error("File reader error");

                                                    return 0;
                                                }
                                            })))));

            ConfigCommand.register(e.getDispatcher());
        }
    }
}
