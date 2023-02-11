package dev.zanckor.example.entity.server;

import dev.zanckor.example.event.dialogevent.StartDialog;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.IOException;

public class NPCEntity extends Cat {


    private String dialogID = "collect_items_dialog";

    public NPCEntity(EntityType<? extends Cat> entityType, Level level) {
        super(entityType, level);
        this.setInvulnerable(true);

        this.setCustomName(Component.literal("Magic Cat"));
        this.setCustomNameVisible(true);
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (!player.level.isClientSide && interactionHand.equals(InteractionHand.MAIN_HAND)) {
            try {
                StartDialog.loadDialog(player, dialogID);
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