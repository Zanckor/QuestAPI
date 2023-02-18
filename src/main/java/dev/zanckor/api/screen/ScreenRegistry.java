package dev.zanckor.api.screen;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.dialog.AbstractDialog;

import java.util.HashMap;

public class ScreenRegistry {
    /**
     * This class registries whichever screen you want to display to screens
     */

    private static HashMap<String, AbstractDialog> dialog = new HashMap<>();
    private static HashMap<String, Class> tracked_quest = new HashMap<>();

    public static void registerDialogScreen(String modid, AbstractDialog screen) {
        dialog.put(modid, screen);
    }

    public static AbstractDialog getDialogScreen(String identifier) {
        AbstractDialog abstractDialog = dialog.get(identifier);

        if (abstractDialog != null) {
            return abstractDialog;
        }

        QuestApiMain.LOGGER.error("Your identifier " + identifier + " is incorrect or you have no screen registered");
        return dialog.get(QuestApiMain.MOD_ID);
    }

    /**
     * TODO: Registry of HUD Tracked Quests and QuestLog
     */
}
