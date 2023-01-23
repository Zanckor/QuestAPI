package com.zanckor.mod.util;

import com.google.gson.Gson;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.DialogReadTemplate;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.mod.QuestApiMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zanckor.mod.QuestApiMain.getReadDialogs;
import static com.zanckor.mod.QuestApiMain.playerData;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID)
public class MCUtil {

    public static Entity getEntityLookinAt(Entity rayTraceEntity, double distance) {
        float playerRotX = rayTraceEntity.getXRot();
        float playerRotY = rayTraceEntity.getYRot();
        Vec3 startPos = rayTraceEntity.getEyePosition();
        float f2 = Mth.cos(-playerRotY * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-playerRotY * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-playerRotX * ((float) Math.PI / 180F));
        float additionY = Mth.sin(-playerRotX * ((float) Math.PI / 180F));
        float additionX = f3 * f4;
        float additionZ = f2 * f4;
        double d0 = distance;
        Vec3 endVec = startPos.add(
                ((double) additionX * d0),
                ((double) additionY * d0),
                ((double) additionZ * d0));

        AABB startEndBox = new AABB(startPos, endVec);
        Entity entity = null;
        for (Entity entity1 : rayTraceEntity.level.getEntities(rayTraceEntity, startEndBox, (val) -> true)) {
            AABB aabb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> optional = aabb.clip(startPos, endVec);
            if (aabb.contains(startPos)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    startPos = optional.orElse(startPos);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3 vec31 = optional.get();
                double d1 = startPos.distanceToSqr(vec31);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getRootVehicle() == rayTraceEntity.getRootVehicle() && !entity1.canRiderInteract()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            startPos = vec31;
                        }
                    } else {
                        entity = entity1;
                        startPos = vec31;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }

    public static BlockHitResult getHitResult(Level level, Player player, float multiplier) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getReachDistance() * multiplier;
        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }


    public static ClientQuestBase getJsonQuest(File file, Gson gson) throws IOException {
        if (!file.exists()) return null;

        FileReader reader = new FileReader(file);
        ClientQuestBase playerQuest = gson.fromJson(reader, ClientQuestBase.class);
        reader.close();

        return playerQuest;
    }

    public static DialogTemplate getJsonDialog(File file, Gson gson) throws IOException {
        if (!file.exists()) return null;

        FileReader reader = new FileReader(file);
        DialogTemplate dialog = gson.fromJson(reader, DialogTemplate.class);
        reader.close();

        return dialog;
    }


    public static void writeDialogRead(Player player, int dialogID) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);

        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

        Path path = Paths.get(getReadDialogs(userFolder).toString(), "\\", "dialog_read.json");
        File file = path.toFile();

        DialogReadTemplate.GlobalID dialog = null;

        if (file.exists()) {
            FileReader reader = new FileReader(file);
            dialog = gson.fromJson(reader, DialogReadTemplate.GlobalID.class);
            reader.close();
        }


        List<DialogReadTemplate.DialogID> dialogIDList;
        if (dialog == null) {
            dialogIDList = new ArrayList<>();
        } else {
            dialogIDList = dialog.getDialog_id();

            for (int i = 0; i < dialogIDList.size(); i++) {
                if (dialogIDList.get(i).getDialog_id() == dialogID) {
                    return;
                }
            }
        }

        dialogIDList.add(new DialogReadTemplate.DialogID(dialogID));
        DialogReadTemplate.GlobalID globalIDClass = new DialogReadTemplate.GlobalID(currentDialog, dialogIDList);

        FileWriter writer = new FileWriter(file);
        writer.write(gson.toJson(globalIDClass));
        writer.flush();
        writer.close();
    }

    public static boolean canReadDialog(Player player, int dialogID) throws IOException {
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

        Path path = Paths.get(getReadDialogs(userFolder).toString(), "\\", "dialog_read.json");
        File file = path.toFile();

        DialogReadTemplate.GlobalID dialog = null;

        if (file.exists()) {
            FileReader reader = new FileReader(file);
            dialog = gson.fromJson(reader, DialogReadTemplate.GlobalID.class);
            reader.close();
        } else {
            return true;
        }


        List<DialogReadTemplate.DialogID> dialogIDList;
        if (dialog != null) {
            dialogIDList = dialog.getDialog_id();

            for (int i = 0; i < dialogIDList.size(); i++) {
                if (dialogIDList.get(i).getDialog_id() == dialogID) {
                    return false;
                }
            }
        }

        return true;
    }

    public static List<List<FormattedCharSequence>> splitText(String text, Font font, int textSize) {
        final List<List<FormattedCharSequence>> textBlocks = new ArrayList<>();

        textBlocks.add(font.split(Component.literal(text), textSize));

        return textBlocks;
    }


    public static void playTextSound() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.LEVER_CLICK, Mth.randomBetween(RandomSource.create(), 0.975f, 1.025f)));
    }
}