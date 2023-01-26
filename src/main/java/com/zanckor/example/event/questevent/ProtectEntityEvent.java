package com.zanckor.example.event.questevent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.quest.QuestDataPacket;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Timer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.zanckor.api.quest.enumquest.EnumQuestType.PROTECT_ENTITY;
import static com.zanckor.mod.util.MCUtil.getJsonClientQuest;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProtectEntityEvent {

    @SubscribeEvent
    public static void failProtectEntity(LivingDeathEvent e) throws IOException {
        if (e.getEntity().level.isClientSide) return;

        for (Player player : e.getEntity().getServer().getPlayerList().getPlayers()) {
            List<Path> protectEntityQuests = LocateHash.getQuestTypeLocation(PROTECT_ENTITY);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (protectEntityQuests == null) return;

            checkEntity(protectEntityQuests, gson, player, e.getEntity());
        }
    }

    public static void checkEntity(List<Path> protectEntityQuests, Gson gson, Player player, Entity entity) throws IOException {
        for (Path path : protectEntityQuests) {
            ClientQuestBase playerQuest = getJsonClientQuest(path.toFile(), gson);
            if (playerQuest == null || !playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) return;

            AbstractQuest quest = TemplateRegistry.getQuestTemplate(PROTECT_ENTITY);

            for (String entityUUID : playerQuest.getQuest_target()) {
                if (!entity.getUUID().toString().equals(entityUUID)) continue;

                quest.handler(player, gson, path.toFile(), playerQuest);
            }
        }
    }


    @SubscribeEvent
    public static void protectEntityQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        List<Path> protectEntityQuests = LocateHash.getQuestTypeLocation(PROTECT_ENTITY);
        if (protectEntityQuests == null) return;

        for (Path path : protectEntityQuests) {
            ClientQuestBase playerQuest = getJsonClientQuest(path.toFile(), MCUtil.gson());
            if(playerQuest != null && playerQuest.isCompleted()) return;

            protectEntity(playerQuest, e.player);
        }
    }

    public static void protectEntity(ClientQuestBase playerQuest, Player player) {
        if (playerQuest != null &&
                playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString()) &&
                !playerQuest.getQuest_target().contains("entity") &&
                Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {

            SendQuestPacket.TO_SERVER(new QuestDataPacket(PROTECT_ENTITY));
        }
    }
}
