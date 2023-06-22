package dev.zanckor.example.common.entity.server;

import dev.zanckor.mod.client.event.StartDialog;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.IOException;

public class NPCEntity extends Villager {
    private String dialogID = "questapi.collect_items_dialog";

    public NPCEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
        this.setInvulnerable(true);
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (!player.level.isClientSide && interactionHand.equals(InteractionHand.MAIN_HAND)) {
            try {
                StartDialog.loadDialog(player, dialogID, "minecraft:cow");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return super.mobInteract(player, interactionHand);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, Integer.MAX_VALUE)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .build();
    }
}