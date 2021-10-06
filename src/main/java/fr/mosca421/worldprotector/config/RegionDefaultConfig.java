package fr.mosca421.worldprotector.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RegionDefaultConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> OP_COMMAND_PERMISSION_LEVEL;

    static {
        BUILDER.push("WorldProtector mod common configuration").build();

        OP_COMMAND_PERMISSION_LEVEL = BUILDER.comment("Default OP level to use WorldProtector commands.").define("command_op_level", 4);

        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}
