package dev.zanckor.example.common.handler.questgoal;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType.XP;

public class XpGoal extends AbstractGoal {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        if (questGoal.getCurrentAmount() == null) return;

        int currentAmount = player.experienceLevel >= questGoal.getAmount() ? questGoal.getAmount() : player.experienceLevel;
        questGoal.setCurrentAmount(currentAmount);
        GsonManager.writeJson(file, userQuest);

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) {
        player.setExperienceLevels(player.experienceLevel - userGoal.getAmount());
    }

    @Override
    public void updateData(ServerPlayer player, File file) {
    }

    @Override
    public Enum getGoalType() {
        return XP;
    }
}
