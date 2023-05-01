package dev.zanckor.mod.common.config.client;

import dev.zanckor.mod.QuestApiMain;
import net.minecraftforge.common.ForgeConfigSpec;

public class RendererConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Integer> QUEST_MARK_UPDATE_COOLDOWN;

    static {
        BUILDER.push("Renderer configuration");

        QUEST_MARK_UPDATE_COOLDOWN = BUILDER.comment("How long ? mark takes to update on change entity data. Lower value = realistic, less optimization")
                .define("Quest Mark ? Update Cooldown", 5);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
