package dev.zanckor.example.common.handler.questgoal;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType.KILL;

public class KillGoal extends AbstractGoal {
    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        //Checks if killed entity equals to target and if it is, checks if current progress is more than target amount
        if (questGoal.getCurrentAmount() >= questGoal.getAmount() || !(questGoal.getTarget().equals(EntityType.getKey(entity.getType()).toString())))
            return;

        questGoal.incrementCurrentAmount(1);
        GsonManager.writeJson(file, userQuest);

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) {

    }

    @Override
    public void updateData(ServerPlayer player, File file) throws IOException {
    }

    @Override
    public Enum getGoalType() {
        return KILL;
    }
}
