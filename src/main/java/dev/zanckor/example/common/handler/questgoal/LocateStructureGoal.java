package dev.zanckor.example.common.handler.questgoal;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.mod.common.config.server.GoalConfig;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.Mathematic;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType.LOCATE_STRUCTURE;

public class LocateStructureGoal extends AbstractGoal {
    public static HashMap<UUID, HashMap<TagKey<Structure>, Vec3>> structurePosition = new HashMap<>();

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserGoal questGoal = userQuest.getQuestGoals().get(indexGoal);
        ServerLevel serverLevel = (ServerLevel) player.level;

        if (questGoal.getCurrentAmount() >= questGoal.getAmount()) return;

        String structureRLString = userQuest.getQuestGoals().get(indexGoal).getTarget();
        ResourceLocation rl = new ResourceLocation(structureRLString);
        TagKey<Structure> structureTagKey = TagKey.create(Registry.STRUCTURE_REGISTRY, rl);
        Optional<HolderSet.Named<Structure>> structureHolder = serverLevel.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).getTag(structureTagKey);


        HashMap<TagKey<Structure>, Vec3> playerStructurePosition = structurePosition.get(player.getUUID());

        if (Timer.canUseWithCooldown(player.getUUID(), "SEARCH_FOR_NEW_STRUCTURE", GoalConfig.LOCATE_STRUCTURE_COOLDOWN.get() * 200))
            searchForNewStructure(player, structureTagKey, serverLevel, playerStructurePosition);


        if (playerStructurePosition != null && playerStructurePosition.containsKey(structureTagKey)) {
            if (Mathematic.vec3NumberBetween(playerStructurePosition.get(structureTagKey), player.getPosition(0), -50, 50)) {

                //If there's any structure type of goal target, increment amount by 1
                questGoal.incrementCurrentAmount(1);
                GsonManager.writeJson(file, userQuest);

                userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                super.handler(player, entity, gson, file, userQuest, indexGoal, questType);
            }
        } else {
            searchForNewStructure(player, structureTagKey, serverLevel, playerStructurePosition);
        }
    }

    public void searchForNewStructure(Player player, TagKey<Structure> structureTagKey, ServerLevel serverLevel, HashMap<TagKey<Structure>, Vec3> playerStructurePosition) {
        //Locate the nearest structure to player and save vec3 pos to structurePosition hashmap
        BlockPos nearestStructure = serverLevel.findNearestMapStructure(structureTagKey, player.blockPosition(), 100, false);

        if (nearestStructure != null) {
            HashMap<TagKey<Structure>, Vec3> structures = playerStructurePosition == null ? new HashMap<>() : playerStructurePosition;
            structures.put(structureTagKey, new Vec3(nearestStructure.getX(), nearestStructure.getY(), nearestStructure.getZ()));

            structurePosition.put(player.getUUID(), structures);
        }
    }


    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) {

    }

    @Override
    public void updateData(ServerPlayer player, File file) throws IOException {
    }

    @Override
    public Enum getGoalType() {
        return LOCATE_STRUCTURE;
    }
}
