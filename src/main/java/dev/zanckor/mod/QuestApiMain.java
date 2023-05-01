package dev.zanckor.mod;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.example.ModExample;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractQuestTracked;
import dev.zanckor.mod.client.screen.questmaker.QuestDefaultScreen;
import dev.zanckor.mod.client.screen.questmaker.QuestGoalSelectorScreen;
import dev.zanckor.mod.common.config.client.RendererConfig;
import dev.zanckor.mod.common.config.client.ScreenConfig;
import dev.zanckor.mod.common.config.server.GoalConfig;
import dev.zanckor.mod.common.menu.MenuHandler;
import dev.zanckor.mod.common.network.NetworkHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static dev.zanckor.mod.QuestApiMain.MOD_ID;

@Mod(MOD_ID)
public class QuestApiMain {
    public static final String MOD_ID = "questapi";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Path serverDirectory, questApi, playerData, serverQuests, serverDialogs, serverNPC, entity_type_list, compoundTag_List;

    public QuestApiMain() {
        LOGGER.debug("Loading QuestAPI");

        new ModExample();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        MenuHandler.REGISTER.register(modEventBus);
        NetworkHandler.register();

        LOGGER.debug("Registering config files");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ScreenConfig.SPEC, "questapi-screen.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RendererConfig.SPEC, "questapi-renderer.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, GoalConfig.SPEC, "questapi-goals.toml");
    }


    public static Path getUserFolder(UUID playerUUID) {
        Path userFolder = Paths.get(playerData.toString(), playerUUID.toString());

        return userFolder;
    }

    public static Path getActiveQuest(Path userFolder) {
        Path activeQuest = Paths.get(userFolder.toString(), "active-quests");

        return activeQuest;
    }

    public static Path getCompletedQuest(Path userFolder) {
        Path completedQuest = Paths.get(userFolder.toString(), "completed-quests");

        return completedQuest;
    }

    public static Path getUncompletedQuest(Path userFolder) {
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        return uncompletedQuest;
    }

    public static Path getReadDialogs(Path userFolder) {
        Path readDialogs = Paths.get(userFolder.toString(), "read-dialogs");

        return readDialogs;
    }


    @Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientEventHandlerRegister {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent e) {
            LOGGER.debug("Registering overlay screens");

            e.registerAboveAll("quest_tracker", (gui, poseStack, partialTick, width, height) -> {
                Player player = Minecraft.getInstance().player;

                if (player != null && !player.isDeadOrDying()) {
                    AbstractQuestTracked abstractQuestTracked = ScreenRegistry.getQuestTrackedScreen(ScreenConfig.QUEST_TRACKED_SCREEN.get());
                    abstractQuestTracked.renderQuestTracked(poseStack, width, height);
                }
            });
        }

        public static KeyMapping questMenu;

        public static KeyMapping registerKey(String name, int keycode) {
            LOGGER.debug("Registering keys");

            final var key = new KeyMapping("key." + QuestApiMain.MOD_ID + "." + name, keycode, "key.categories.QuestApi");

            return key;
        }

        @SubscribeEvent
        public static void keyInit(RegisterKeyMappingsEvent e) {
            questMenu = registerKey("quest_menu", InputConstants.KEY_K);

            e.register(questMenu);
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent e) {
            e.enqueueWork(() -> MenuScreens.register(MenuHandler.QUEST_DEFAULT_MENU.get(), QuestDefaultScreen::new));
        }
    }
}
