package dev.zanckor.example.server.event.questevent;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.ServerHandler;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Mathematic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.MOVE_TO;
import static java.lang.Integer.parseInt;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MoveToEvent {

    @SubscribeEvent
    public static void moveToQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player == null || e.side.isClient() || e.player.getServer().getTickCount() % 20 != 0) return;

        List<Path> moveToQuests = LocateHash.getQuestTypeLocation(MOVE_TO);

        if (moveToQuests != null) {
            for (Path path : moveToQuests) {
                UserQuest playerQuest = (UserQuest) GsonManager.getJsonClass(path.toFile(), UserQuest.class);
                if (playerQuest == null || playerQuest.isCompleted()) continue;

                moveTo(playerQuest, (ServerPlayer) e.player);
            }
        }
    }

    public static void moveTo(UserQuest userQuest, ServerPlayer player) throws IOException {
        int xCoord = 0, yCoord = 0, zCoord = 0;

        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

            if (!(questGoal.getType().equals(MOVE_TO.toString()))) continue;
            String coord = questGoal.getTarget().substring(1);

            if (questGoal.getTarget().contains("x")) {
                xCoord = parseInt(coord);
            }
            if (questGoal.getTarget().contains("y")) {
                yCoord = parseInt(coord);
            }
            if (questGoal.getTarget().contains("z")) {
                zCoord = parseInt(coord);
            }
        }

        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (Mathematic.numberBetween(playerCoord.x, xCoord - 10, xCoord + 10) && Mathematic.numberBetween(playerCoord.y, yCoord - 10, yCoord + 10) && Mathematic.numberBetween(playerCoord.z, zCoord - 10, zCoord + 10)) {
            ServerHandler.questHandler(MOVE_TO, player, null);
        }
    }
}
