package fr.mosca421.worldprotector.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ServerConfigBuilder {

    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> OP_COMMAND_PERMISSION_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> WP_COMMAND_ALTERNATIVE;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_REGION_PRIORITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_REGION_PRIORITY_INC;

    public final static String WP = "wp";
    public final static String WP_ALT = "w-p";
    public final static String WP_ALT2 = "worldprotector";
    public static String WP_CMD;
    public static final String[] WP_CMDS = new String[]{WP, WP_ALT, WP_ALT2};

    static {
        final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("WorldProtector mod server configuration").build();

        OP_COMMAND_PERMISSION_LEVEL = BUILDER.comment("Default OP level to use WorldProtector commands.")
                .defineInRange("command_op_level", 4, 1, 4);

        WP_COMMAND_ALTERNATIVE = BUILDER.comment("Default command alternative used in quick commands in chat.\nThis is only important if another mod uses the /wp command (like Journey Map). Defaults to 0.\n" +
                " 0 -> /wp\n 1 -> /w-p\n 2 -> /worldprotector")
                .defineInRange("wp_command_alt", 0, 0, 2);

        DEFAULT_REGION_PRIORITY = BUILDER.comment("Default region priority for newly created regions.")
                .defineInRange("default_region_priority", 2, 0, Integer.MAX_VALUE);

        // TODO: should be a not synced client config
        DEFAULT_REGION_PRIORITY_INC = BUILDER.comment("Default region priority increment for usage in commands.")
                .defineInRange("default_region_priority_inc", 1, 1, 100);

        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();

        int wpCmdIdx = WP_COMMAND_ALTERNATIVE.get();
        WP_CMD = WP_CMDS[wpCmdIdx < 0 || wpCmdIdx > 2 ? 0 : wpCmdIdx];
    }


    private static boolean isInModList(String modid) {
        return ModList.get().isLoaded(modid);
    }

    // TODO: Testen
    public static void setup (FMLCommonSetupEvent event) {
        if (isInModList("JourneyMap")) {
            ServerConfigBuilder.WP_CMD = ServerConfigBuilder.WP_CMDS[1];
        }
    }

}
