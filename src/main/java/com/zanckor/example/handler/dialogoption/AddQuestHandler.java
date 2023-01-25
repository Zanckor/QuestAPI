package com.zanckor.example.handler.dialogoption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogOption;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.ServerQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractRequirement;
import com.zanckor.api.quest.enumquest.EnumQuestRequirement;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Timer;
import net.minecraft.resources.ResourceLocation;
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

public class AddQuestHandler extends AbstractDialogOption {


    @Override
    public void handler(Player player, DialogTemplate dialog, int optionID) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int currentDialog = LocateHash.currentDialog.get(player);
        DialogTemplate.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(optionID);
        String quest = "id_" + option.getQuest_id() + ".json";

        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (option.getType().equals(EnumOptionType.ADD_QUEST.toString())) {
            if (Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getUncompletedQuest(userFolder).toString(), quest))) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.closeDialog());

                return;
            }


            for (File file : serverQuests.toFile().listFiles()) {
                Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());

                if (file.getName().equals(quest)) {
                    ServerQuestBase serverQuest = MCUtil.getJsonServerQuest(file, gson);
                    AbstractRequirement requirement = TemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(serverQuest.getRequirements_type()));

                    if (!requirement.handler(player, serverQuest)) {
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

                    LocateHash.registerQuestByID(option.getQuest_id(), path);
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
