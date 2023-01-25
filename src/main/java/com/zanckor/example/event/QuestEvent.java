package com.zanckor.example.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogRequirement;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumRequirementType;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.quest.QuestDataPacket;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Mathematic;
import com.zanckor.mod.util.Timer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.zanckor.api.quest.enumquest.EnumQuestType.*;
import static com.zanckor.mod.util.MCUtil.getJsonClientQuest;
import static com.zanckor.mod.util.MCUtil.playTextSound;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QuestEvent {
    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getSource().getEntity() instanceof Player)) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(KILL));
    }

    @SubscribeEvent
    public static void recollectPickUpQuest(PlayerEvent.ItemPickupEvent e) throws IOException {
        if (e.getEntity().level.isClientSide) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));

        loadDialog(e.getEntity(), 0);
    }
    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemCraftedEvent e) {
        if (e.getEntity().level.isClientSide) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));
    }
    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) {
        if (e.getEntity().level.isClientSide) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));
    }


    @SubscribeEvent
    public static void interactWithNPC(PlayerInteractEvent.EntityInteract e) {
        if (e.getEntity().level.isClientSide || e.getHand() == InteractionHand.MAIN_HAND) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(INTERACT_ENTITY));
    }
    @SubscribeEvent
    public static void failProtectEntity(LivingDeathEvent e) {
        if (e.getEntity().level.isClientSide) return;

        for (Player player : e.getEntity().getServer().getPlayerList().getPlayers()) {
            List<Path> protectEntityQuests = LocateHash.getQuestTypeLocation(PROTECT_ENTITY);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (protectEntityQuests == null) return;

            for (Path path : protectEntityQuests) {
                try {
                    File file = path.toFile();
                    ClientQuestBase playerQuest = getJsonClientQuest(file, gson);

                    if (playerQuest == null || !playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) return;
                    AbstractQuest quest = TemplateRegistry.getQuestTemplate(PROTECT_ENTITY);

                    for (String entityUUID : playerQuest.getQuest_target()) {
                        if (!e.getEntity().getUUID().toString().equals(entityUUID)) continue;

                        quest.handler(player, gson, file, playerQuest);
                    }

                } catch (IOException exception) {
                    QuestApiMain.LOGGER.error(exception.getMessage());
                }
            }
        }
    }
    @SubscribeEvent
    public static void protectEntityQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        List<Path> protectEntityQuests = LocateHash.getQuestTypeLocation(PROTECT_ENTITY);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (protectEntityQuests == null) return;
        for (Path path : protectEntityQuests) {
            File file = path.toFile();
            ClientQuestBase playerQuest = getJsonClientQuest(file, gson);
            if (playerQuest == null) return;

            protectEntity(playerQuest, e.player);
        }
    }
    @SubscribeEvent
    public static void moveToQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        List<Path> moveToQuests = LocateHash.getQuestTypeLocation(MOVE_TO);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (moveToQuests != null) {
            for (Path path : moveToQuests) {
                File file = path.toFile();
                ClientQuestBase playerQuest = getJsonClientQuest(file, gson);
                if (playerQuest == null) continue;

                moveTo(playerQuest, e.player);
            }
        }
    }


    public static void moveTo(ClientQuestBase playerQuest, Player player) {
        if (!playerQuest.getQuest_type().equals(MOVE_TO.toString()))
            return;

        Integer xCoord = Integer.valueOf(playerQuest.getQuest_target().get(0));
        Integer yCoord = Integer.valueOf(playerQuest.getQuest_target().get(1));
        Integer zCoord = Integer.valueOf(playerQuest.getQuest_target().get(2));
        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (Mathematic.numberBetween(playerCoord.x, xCoord - 10, xCoord + 10) && Mathematic.numberBetween(playerCoord.y, yCoord - 10, yCoord + 10) && Mathematic.numberBetween(playerCoord.z, zCoord - 10, zCoord + 10)) {
            SendQuestPacket.TO_SERVER(new QuestDataPacket(MOVE_TO));
        }
    }

    public static void protectEntity(ClientQuestBase playerQuest, Player player) {
        if (playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString()) && !playerQuest.getQuest_target().contains("entity")) {
            if (Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
                SendQuestPacket.TO_SERVER(new QuestDataPacket(PROTECT_ENTITY));
            }
        }
    }


    //TODO Change this
    public static void loadDialog(Player player, int globalDialogID) throws IOException {
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
        Path path = DialogTemplate.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        DialogTemplate dialog = MCUtil.getJsonDialog(dialogFile, gson);

        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            if (dialog.getDialog().get(dialog_id).getRequirements().getType() == null) continue;

            EnumRequirementType requirementType = EnumRequirementType.valueOf(dialog.getDialog().get(dialog_id).getRequirements().getType());
            AbstractDialogRequirement dialogRequirement = TemplateRegistry.getDialogRequirement(requirementType);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id)) return;
        }
    }
}
