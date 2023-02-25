package dev.zanckor.mod.common.util;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.ReadDialog;
import dev.zanckor.mod.QuestApiMain;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.zanckor.mod.QuestApiMain.*;

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
        Vec3 endVec = startPos.add(((double) additionX * d0), ((double) additionY * d0), ((double) additionZ * d0));

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


    public static void writeDialogRead(Player player, int dialogID) throws IOException {
        String globalDialog = LocateHash.currentGlobalDialog.get(player);


        Path userFolder = Paths.get(QuestApiMain.playerData.toString(), player.getUUID().toString());

        Path path = Paths.get(QuestApiMain.getReadDialogs(userFolder).toString(), "\\", "dialog_read.json");
        File file = path.toFile();

        ReadDialog.GlobalID dialog = null;

        if (file.exists()) {
            FileReader reader = new FileReader(file);
            dialog = GsonManager.gson().fromJson(reader, ReadDialog.GlobalID.class);
            reader.close();
        }


        List<ReadDialog.DialogID> dialogIDList;
        if (dialog == null) {
            dialogIDList = new ArrayList<>();
        } else {
            dialogIDList = dialog.getDialog_id();

            for (ReadDialog.DialogID id : dialogIDList) {
                if (id.getDialog_id() == dialogID) {
                    return;
                }
            }
        }

        dialogIDList.add(new ReadDialog.DialogID(dialogID));
        ReadDialog.GlobalID globalIDClass = new ReadDialog.GlobalID(globalDialog, dialogIDList);

        FileWriter writer = new FileWriter(file);
        writer.write(GsonManager.gson().toJson(globalIDClass));
        writer.flush();
        writer.close();
    }

    public static boolean isReadDialog(Player player, int dialogID) throws IOException {
        Path userFolder = Paths.get(QuestApiMain.playerData.toString(), player.getUUID().toString());

        Path path = Paths.get(QuestApiMain.getReadDialogs(userFolder).toString(), "\\", "dialog_read.json");
        File file = path.toFile();
        ReadDialog.GlobalID dialog;

        if (!file.exists()) return false;

        FileReader reader = new FileReader(file);
        dialog = GsonManager.gson().fromJson(reader, ReadDialog.GlobalID.class);
        reader.close();

        List<ReadDialog.DialogID> dialogIDList;
        if (dialog != null) {
            dialogIDList = dialog.getDialog_id();

            for (ReadDialog.DialogID id : dialogIDList) {
                if (id.getDialog_id() == dialogID) {
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean hasQuest(String quest, Path userFolder) {
        return Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getUncompletedQuest(userFolder).toString(), quest));
    }

    public static Entity getEntityByUUID(ServerLevel level, UUID uuid) {
        for (Entity entity : level.getAllEntities()) {
            if (entity.getUUID().equals(uuid)) return entity;
        }

        return null;
    }


}