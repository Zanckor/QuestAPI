package dev.zanckor.example.server.event.questevent;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.ServerHandler;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.PROTECT_ENTITY;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProtectEntityEvent {

    @SubscribeEvent
    public static void failProtectEntity(LivingDeathEvent e) throws IOException {
        if (e.getEntity().level.isClientSide) return;

        for (Player player : e.getEntity().getServer().getPlayerList().getPlayers()) {
            List<Path> protectEntityQuests = LocateHash.getQuestTypeLocation(PROTECT_ENTITY);
            if (protectEntityQuests == null) continue;

            checkEntity(protectEntityQuests, player, e.getEntity());
        }
    }

    public static void checkEntity(List<Path> protectEntityQuests, Player player, Entity entity) throws IOException {
        if (protectEntityQuests == null) return;

        for (Path path : protectEntityQuests) {
            UserQuest playerQuest = (UserQuest) GsonManager.getJson(path.toFile(), UserQuest.class);
            if (playerQuest == null || !playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) continue;

            AbstractQuest quest = TemplateRegistry.getQuestTemplate(PROTECT_ENTITY);

            for (String entityUUID : playerQuest.getQuest_target()) {
                if (!entity.getUUID().toString().equals(entityUUID)) continue;

                quest.handler(player, null, GsonManager.gson(), path.toFile(), playerQuest);
            }
        }
    }


    @SubscribeEvent
    public static void protectEntityQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.side.isClient()) return;
        List<Path> protectEntityQuests = LocateHash.getQuestTypeLocation(PROTECT_ENTITY);

        if (protectEntityQuests == null) return;

        for (Path path : protectEntityQuests) {
            UserQuest userQuest = (UserQuest) GsonManager.getJson(path.toFile(), UserQuest.class);
            if (userQuest != null && userQuest.isCompleted()) continue;

            protectEntity(userQuest, (ServerPlayer) e.player);
        }
    }

    public static void protectEntity(UserQuest playerQuest, ServerPlayer player) throws IOException {
        if (playerQuest != null && playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString()) && !playerQuest.getQuest_target().contains("entity") && Timer.canUseWithCooldown(player.getUUID(), playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {

            ServerHandler.questHandler(PROTECT_ENTITY, player, null);
        }
    }
}
