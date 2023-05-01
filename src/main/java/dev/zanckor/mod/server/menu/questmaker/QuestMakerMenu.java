package dev.zanckor.mod.server.menu.questmaker;

import dev.zanckor.mod.common.menu.MenuHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class QuestMakerMenu extends AbstractContainerMenu {
    public QuestMakerMenu(int id) {
        super(MenuHandler.QUEST_DEFAULT_MENU.get(), id);
    }



    @Override
    public ItemStack quickMoveStack(Player player, int stack) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
