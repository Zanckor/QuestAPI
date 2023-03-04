package dev.zanckor.mod.common.config.client;

import dev.zanckor.mod.QuestApiMain;
import net.minecraftforge.common.ForgeConfigSpec;

public class ScreenConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<String> QUEST_TRACKED_SCREEN;
    public static ForgeConfigSpec.ConfigValue<String> QUEST_LOG_SCREEN;

    static {
        BUILDER.push("Screen configuration");

        QUEST_TRACKED_SCREEN = BUILDER.comment("Which quest tracked design you want to see")
                .define("Quest Tracked Screen", QuestApiMain.MOD_ID);

        QUEST_LOG_SCREEN = BUILDER.comment("Which design you want to see on quest log")
                .define("Quest Log Screen", QuestApiMain.MOD_ID);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
