package dev.zanckor.mod.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.npc.entity_type_tag.codec.EntityTypeTagDialog;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractQuestLog;
import dev.zanckor.mod.common.config.client.RendererConfig;
import dev.zanckor.mod.common.config.client.ScreenConfig;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;

import java.io.IOException;
import java.util.Map;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void keyOpenScreen(InputEvent.Key e) throws IOException {
        if (QuestApiMain.ClientEventHandlerRegister.questMenu.isDown()) {
            AbstractQuestLog questLogScreen = ScreenRegistry.getQuestLogScreen(ScreenConfig.QUEST_LOG_SCREEN.get());

            Minecraft.getInstance().setScreen(questLogScreen.modifyScreen());
        }
    }

    @SubscribeEvent
    public static void loadHashMaps(ClientPlayerNetworkEvent.LoggingIn e) {
        ClientHandler.userQuest = null;
    }

    @SubscribeEvent
    public static void renderNPCQuestMarker(RenderLivingEvent.Pre e) {
        Player player = Minecraft.getInstance().player;
        PoseStack poseStack = e.getPoseStack();
        Font font = Minecraft.getInstance().font;

        float distance = player.distanceTo(e.getEntity());
        double alphaMultiplier = Math.pow(8, ((75 - distance) / 120)) - 6.65;
        int flatColor = 0XFFFF00;

        int color = (int) (alphaMultiplier * 255.0F) << 24 | flatColor;

        if (player.distanceTo(e.getEntity()) < 15) {
            if (checkEntityTagIsValid(e.getEntity()) || checkEntityTypeIsValid(e.getEntity())) {
                poseStack.pushPose();

                poseStack.translate(-0.1, e.getEntity().getBbHeight() + 1.25, 0);
                poseStack.scale(0.15f, 0.125f, 0.15f);
                poseStack.mulPose(new Quaternionf().rotateXYZ((float) Math.toRadians(180), (float) Math.toRadians(player.getYHeadRot() + 180), 0));


                font.draw(poseStack, "!", 0, 0, color);

                poseStack.popPose();
            }
        }
    }

    public static boolean checkEntityTypeIsValid(LivingEntity entity) {
        EntityType entityType = entity.getType();

        if (entity.getPersistentData().getBoolean("beingRenderedOnInventory")) return false;

        if (entity.getPersistentData().contains("availableForDialog")) {
            return entity.getPersistentData().getBoolean("availableForDialog");
        }

        if (ClientHandler.availableEntityTypeForQuest != null) {

            entity.getPersistentData().putBoolean("availableForDialog", ClientHandler.availableEntityTypeForQuest.contains(entityType));
            return ClientHandler.availableEntityTypeForQuest.contains(entityType);
        }

        return false;
    }

    public static boolean checkEntityTagIsValid(LivingEntity entity) {
        for (Map.Entry<String, String> entry : ClientHandler.availableEntityTagForQuest.entrySet()) {

            if (Timer.canUseWithCooldown(entity.getUUID(), "UPDATE_MARKER", RendererConfig.QUEST_MARK_UPDATE_COOLDOWN.get())) {
                Timer.updateCooldown(entity.getUUID(), "UPDATE_MARKER", RendererConfig.QUEST_MARK_UPDATE_COOLDOWN.get());

                CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(entity);
                String value = entry.getValue();

                EntityTypeTagDialog entityTypeDialog = GsonManager.gson.fromJson(value, EntityTypeTagDialog.class);

                conditions:
                for (EntityTypeTagDialog.EntityTypeTagDialogCondition conditions : entityTypeDialog.getConditions()) {
                    boolean tagCompare;

                    switch (conditions.getLogic_gate()) {
                        case OR: {
                            for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
                                if (entityNBT.get(nbt.getTag()) == null) {
                                    continue;
                                }

                                tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());

                                entity.getPersistentData().putBoolean("availableForDialog", tagCompare);
                                if (tagCompare) return true;
                            }
                            break;
                        }

                        case AND: {
                            boolean shouldAddMarker = false;

                            for (EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT nbt : conditions.getNbt()) {

                                if (entityNBT.get(nbt.getTag()) != null) {
                                    tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());
                                } else {
                                    tagCompare = false;
                                }

                                shouldAddMarker = tagCompare;

                                if (!tagCompare) break;
                            }

                            entity.getPersistentData().putBoolean("availableForDialog", shouldAddMarker);
                            if (shouldAddMarker) {
                                return true;
                            }

                            break;
                        }
                    }
                }
            }

            if (entity.getPersistentData().getBoolean("beingRenderedOnInventory")) return false;

            if (entity.getPersistentData().contains("availableForDialog")) {
                return entity.getPersistentData().getBoolean("availableForDialog");
            }
        }

        return false;
    }
}
