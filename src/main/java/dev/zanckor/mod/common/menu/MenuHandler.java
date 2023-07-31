package dev.zanckor.mod.common.menu;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.server.menu.questmaker.QuestMakerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuHandler {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, QuestApiMain.MOD_ID);

    public static final RegistryObject<MenuType<QuestMakerMenu>> QUEST_DEFAULT_MENU = REGISTER.register("quest_default_menu",
            () -> IForgeMenuType.create(((windowId, inv, data) -> new QuestMakerMenu(windowId))));
}
