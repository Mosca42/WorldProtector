package fr.mosca421.worldprotector.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.config.ServerConfigBuilder;
import fr.mosca421.worldprotector.util.MessageUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;

public class CommandWorldProtector {

    private CommandWorldProtector() {
    }

    public static LiteralArgumentBuilder<CommandSource> register() {
        return withSubCommands(Commands.literal(Command.WP.toString()));
    }

    public static LiteralArgumentBuilder<CommandSource> registerAlternate1() {
        return withSubCommands(Commands.literal(Command.W_P.toString()));
    }

    public static LiteralArgumentBuilder<CommandSource> registerAlternate2() {
        return withSubCommands(Commands.literal(Command.WP_LONG.toString()));
    }

    private static LiteralArgumentBuilder<CommandSource> withSubCommands(LiteralArgumentBuilder<CommandSource> baseCommand) {
        return baseCommand
                .requires(cs -> cs.hasPermissionLevel(ServerConfigBuilder.OP_COMMAND_PERMISSION_LEVEL.get()))
                .executes(ctx -> giveHelp(ctx.getSource()))
                .then(Commands.literal(Command.HELP.toString())
                        .executes(ctx -> giveHelp(ctx.getSource())))
                .then(CommandRegion.REGION_COMMAND)
                .then(CommandExpand.EXPAND_COMMAND)
                .then(CommandFlag.FLAG_COMMAND)
                .then(CommandPlayer.PLAYER_COMMAND);
    }

    private static int giveHelp(CommandSource source) {
        try {
            PlayerEntity player = source.asPlayer();
            MessageUtils.promptBaseCommandHelp(player);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;

    }
}
