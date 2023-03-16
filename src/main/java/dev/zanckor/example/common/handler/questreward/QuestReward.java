package dev.zanckor.example.common.handler.questreward;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.codec.ServerQuest;
import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.zanckor.mod.QuestApiMain.*;

public class QuestReward extends AbstractReward {

    /**
     * Type of reward, gives player another quest (multistage) set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @param rewardIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        String quest = serverQuest.getRewards().get(rewardIndex).getTag() + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) return;

            Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());
            ServerQuest gaveServerQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks all quest requirements and return if player hasn't any requirement
            for (int requirementIndex = 0; requirementIndex < gaveServerQuest.getRequirements().size(); requirementIndex++) {
                AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(gaveServerQuest.getRequirements().get(requirementIndex).getType()));

                if (!requirement.handler(player, gaveServerQuest, requirementIndex)) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandler::closeDialog);
                    return;
                }
            }

            FileWriter writer = new FileWriter(path.toFile());
            UserQuest userQuest = UserQuest.createQuest(gaveServerQuest, path);
            GsonManager.gson().toJson(userQuest, writer);
            writer.close();

            if (userQuest.hasTimeLimit()) {
                Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
            }

            LocateHash.registerQuestByID(userQuest.getId(), path);

            break;
        }
    }
}