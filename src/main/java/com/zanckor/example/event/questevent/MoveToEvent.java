package com.zanckor.example.event.questevent;

import com.zanckor.api.database.LocateHash;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.quest.QuestDataPacket;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Mathematic;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.zanckor.api.quest.enumquest.EnumQuestType.MOVE_TO;
import static com.zanckor.mod.util.MCUtil.getJsonClientQuest;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MoveToEvent {

    @SubscribeEvent
    public static void moveToQuest(TickEvent.PlayerTickEvent e) throws IOException {


        if (e.player.level.isClientSide) {
            if (e.player.tickCount % 20 == 0) {

            }
        } else {
            if (e.player == null || e.player.getServer().getTickCount() % 20 != 0)
                return;

            List<Path> moveToQuests = LocateHash.getQuestTypeLocation(MOVE_TO);

            if (moveToQuests != null) {
                for (Path path : moveToQuests) {
                    ClientQuestBase playerQuest = getJsonClientQuest(path.toFile(), MCUtil.gson());
                    if (playerQuest == null) continue;

                    moveTo(playerQuest, e.player);
                }
            }
        }
    }

    public static void moveTo(ClientQuestBase playerQuest, Player player) {
        if (!playerQuest.getQuest_type().equals(MOVE_TO.toString())) return;

        Integer xCoord = Integer.valueOf(playerQuest.getQuest_target().get(0));
        Integer yCoord = Integer.valueOf(playerQuest.getQuest_target().get(1));
        Integer zCoord = Integer.valueOf(playerQuest.getQuest_target().get(2));
        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (Mathematic.numberBetween(playerCoord.x, xCoord - 10, xCoord + 10) && Mathematic.numberBetween(playerCoord.y, yCoord - 10, yCoord + 10) && Mathematic.numberBetween(playerCoord.z, zCoord - 10, zCoord + 10)) {
            SendQuestPacket.TO_SERVER(new QuestDataPacket(MOVE_TO));
        }
    }
}
