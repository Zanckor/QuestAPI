package dev.zanckor.mod.common.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class GoalConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Integer> LOCATE_STRUCTURE_COOLDOWN;

    static {
        BUILDER.push("Goals configuration");

        LOCATE_STRUCTURE_COOLDOWN = BUILDER.comment("What time should take to check again if player is close enough to an structure.")
                .comment("*Don't recommended set less than 10s.")
                .comment("*Time is counted with Ticks. 1s = 20 ticks")
                .define("Locate Structure Cooldown", 200);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
