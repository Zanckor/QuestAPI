package com.zanckor.example.handler.dialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.dialog.abstractdialog.AbstractDialog;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.ServerQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractRequirement;
import com.zanckor.api.quest.enumquest.EnumQuestRequirement;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.example.event.QuestEvent;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.Timer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.zanckor.api.quest.enumquest.EnumQuestType.PROTECT_ENTITY;
import static com.zanckor.mod.QuestApiMain.*;
import static com.zanckor.mod.QuestApiMain.getUncompletedQuest;

public class AddQuestHandler extends AbstractDialog {


    @Override
    public void handler(Player player, DialogTemplate dialog, int optionID) throws IOException {
        int currentDialog = QuestEvent.currentDialog.get(player);
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        DialogTemplate.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(optionID);

        if (option.getType().equals(EnumOptionType.ADD_QUEST.toString())) {
            String quest = "id_" + option.getQuest_id() + ".json";

            if (Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getUncompletedQuest(userFolder).toString(), quest))) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.closeDialog());

                return;
            }


            for (File file : serverQuests.toFile().listFiles()) {
                Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                if (file.getName().equals(quest)) {
                    FileReader reader = new FileReader(file);
                    ServerQuestBase serverQuest = gson.fromJson(reader, ServerQuestBase.class);
                    reader.close();

                    AbstractRequirement requirement = TemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(serverQuest.getRequirements_type()));

                    if (!requirement.handler(player, serverQuest)) {
                        player.sendSystemMessage(Component.literal("Player " + player.getScoreboardName() + " doesn't have the requirements to access to this quest"));
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.closeDialog());

                        return;
                    }

                    FileWriter writer = new FileWriter(path.toFile());
                    ClientQuestBase playerQuest = ClientQuestBase.createQuest(serverQuest, path);
                    gson.toJson(playerQuest, writer);
                    writer.close();

                    if (playerQuest.isHasTimeLimit()) {
                        Timer.updateCooldown(player.getUUID(), "id_" + option.getQuest_id(), playerQuest.getTimeLimitInSeconds());
                    }

                    if (playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) {
                        protectEntityQuest(playerQuest, player.level, player, serverQuest, path, gson, option.getQuest_id());
                    }

                    break;
                }
            }
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.closeDialog());
    }


    private static void protectEntityQuest(ClientQuestBase playerQuest, Level level, Player player, ServerQuestBase serverQuest, Path path, Gson gson, int questID) throws IOException {
        EntityType entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(playerQuest.getQuest_target().get(0)));
        UUID playerUUID = player.getUUID();

        Entity entity = entityType.create(level);
        entity.setPos(player.getPosition(0));

        level.addFreshEntity(entity);

        FileWriter protectEntityWriter = new FileWriter(path.toFile());
        ClientQuestBase protectEntityPlayerQuest = ClientQuestBase.createQuest(serverQuest, path);
        List<String> list = new ArrayList<>();

        list.add(entity.getUUID().toString());

        protectEntityPlayerQuest.setQuest_target(list);
        gson.toJson(protectEntityPlayerQuest, protectEntityWriter);
        protectEntityWriter.close();

        Timer.updateCooldown(playerUUID, "id_" + questID, playerQuest.getTimeLimitInSeconds());
    }
}
