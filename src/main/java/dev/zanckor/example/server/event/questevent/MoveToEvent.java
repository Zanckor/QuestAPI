package dev.zanckor.example.server.event.questevent;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.QuestDataPacket;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Mathematic;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MoveToEvent {

    @SubscribeEvent
    public static void moveToQuest(TickEvent.PlayerTickEvent e) throws IOException {


        if (!e.player.level.isClientSide) {
            if (e.player == null || e.player.getServer().getTickCount() % 20 != 0)
                return;

            List<Path> moveToQuests = LocateHash.getQuestTypeLocation(EnumQuestType.MOVE_TO);

            if (moveToQuests != null) {
                for (Path path : moveToQuests) {
                    UserQuest playerQuest = (UserQuest) GsonManager.getJson(path.toFile(), UserQuest.class);
                    if (playerQuest == null || playerQuest.isCompleted()) continue;

                    moveTo(playerQuest, e.player);
                }
            }
        }
    }

    public static void moveTo(UserQuest playerQuest, Player player) {
        if (!playerQuest.getQuest_type().equals(EnumQuestType.MOVE_TO.toString())) return;

        Integer xCoord = Integer.valueOf(playerQuest.getQuest_target().get(0));
        Integer yCoord = Integer.valueOf(playerQuest.getQuest_target().get(1));
        Integer zCoord = Integer.valueOf(playerQuest.getQuest_target().get(2));
        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (Mathematic.numberBetween(playerCoord.x, xCoord - 10, xCoord + 10) && Mathematic.numberBetween(playerCoord.y, yCoord - 10, yCoord + 10) && Mathematic.numberBetween(playerCoord.z, zCoord - 10, zCoord + 10)) {
            SendQuestPacket.TO_SERVER(new QuestDataPacket(EnumQuestType.MOVE_TO));
        }
    }
}
