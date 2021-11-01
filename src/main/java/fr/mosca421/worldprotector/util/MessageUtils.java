package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.command.Command;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import static fr.mosca421.worldprotector.config.ServerConfigBuilder.DEFAULT_REGION_PRIORITY_INC;
import static fr.mosca421.worldprotector.config.ServerConfigBuilder.WP_CMD;
import static net.minecraft.util.text.TextComponentUtils.*;

public final class MessageUtils {

    private MessageUtils() {
    }

    public static void sendMessage(PlayerEntity player, ITextComponent textComponent) {
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public static void sendMessage(PlayerEntity player, String translationKey) {
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUniqueID());
    }

    public static void sendStatusMessage(PlayerEntity player, String translationKey){
        player.sendStatusMessage(new TranslationTextComponent(translationKey), true);
    }

    public static void sendStatusMessage(PlayerEntity player, ITextComponent textComponent) {
        player.sendStatusMessage(textComponent, true);
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }

    public static void sendRegionInfoCommand(String regionName, PlayerEntity player) {
        RegionManager.get().getRegion(regionName).ifPresent(region -> {
            BlockPos target = region.getTpTarget();
            String regionInfoCommand = "/" + WP_CMD + " " + Command.REGION + " " + Command.INFO + " " + regionName;
            String regionTeleportCommand = "/tp @s " + target.getX() + " " + target.getY() + " " + target.getZ();

            IFormattableTextComponent regionMsg = new StringTextComponent("Region '")
                    .appendSibling(buildRunCommandLink(regionName, regionInfoCommand,
                            TextFormatting.GREEN, "chat.link.hover.region.info"))
                    .appendSibling(new StringTextComponent("': ").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RESET))))
                    .appendSibling(buildRunCommandLink(target.getX() + ", " + target.getY() + ", " + target.getZ(), regionTeleportCommand,
                            TextFormatting.GREEN, "chat.link.hover.region.tp" ));
            sendMessage(player, regionMsg);
        });
    }

    public static IFormattableTextComponent buildRunCommandLink(String linkText, String command, TextFormatting color, String hoverText){
        return wrapWithSquareBrackets(new StringTextComponent(linkText))
        .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(color))
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent(hoverText))));
    }

    public static IFormattableTextComponent buildSuggestCommandLink(String linkText, String command, TextFormatting color, String hoverText){
        return wrapWithSquareBrackets(new StringTextComponent(linkText))
                .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(color))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent(hoverText))));
    }

    public static IFormattableTextComponent buildDimensionTeleportLink(IRegion region) {
        BlockPos target = region.getTpTarget();
        String dim = region.getDimension().getLocation().toString();
        String commandText = dim + "@ [" + target.getX() + ", " + target.getY() + ", " + target.getZ() + "]";
        String teleportCommand = "/execute in " + dim + " run tp @s " + target.getX() + " " + target.getY() + " " + target.getZ();
        return buildRunCommandLink(commandText, teleportCommand, TextFormatting.GREEN, "chat.link.hover.region.tp");
    }

    public static void promptFlagCommandHelp(PlayerEntity player) {
        sendMessage(player, buildHelpHeader("help.flags.header"));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.flags.1", Command.FLAG, Command.ADD));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.flags.2", Command.FLAG, Command.REMOVE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.flags.3", Command.FLAG, Command.LIST));
        sendMessage(player, new StringTextComponent(""));
    }

    public static void promptExpandCommandHelp(PlayerEntity player) {
        sendMessage(player, buildHelpHeader("help.expand.header"));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.expand.1", Command.EXPAND, Command.VERT));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.expand.2", Command.EXPAND, Command.DEFAULT_Y));
        sendMessage(player, new StringTextComponent(""));
    }

    public static void promptPlayerCommandHelp(PlayerEntity player) {
        sendMessage(player, buildHelpHeader("help.players.header"));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.players.1", Command.PLAYER, Command.ADD));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.players.2", Command.PLAYER, Command.REMOVE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.players.3", Command.PLAYER, Command.ADD_OFFLINE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.players.4", Command.PLAYER, Command.REMOVE_OFFLINE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.players.5", Command.PLAYER, Command.LIST));
        sendMessage(player, new StringTextComponent(""));
    }

    public static void promptRegionHelp(PlayerEntity player) {
        sendMessage(player, MessageUtils.buildHelpHeader("help.region.header"));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.1", Command.REGION, Command.DEFINE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.2", Command.REGION, Command.REDEFINE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.3", Command.REGION, Command.REMOVE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.4", Command.REGION, Command.LIST));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.5", Command.REGION, Command.INFO));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.6", Command.REGION, Command.SET_PRIORITY));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.7", Command.REGION, Command.TELEPORT_SHORT));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.8", Command.REGION, Command.DEACTIVATE));
        sendMessage(player, MessageUtils.buildHelpSuggestionLink("help.region.9", Command.REGION,  Command.MUTE));
        sendMessage(player, new StringTextComponent(""));
    }

    public static void promptBaseCommandHelp(PlayerEntity player) {
        sendMessage(player, buildHelpHeader("help.wp.header"));
        sendMessage(player, MessageUtils.buildHelpLink("help.wp.1", Command.EXPAND));
        sendMessage(player, MessageUtils.buildHelpLink("help.wp.4", Command.REGION));
        sendMessage(player, MessageUtils.buildHelpLink("help.wp.2", Command.FLAG));
        sendMessage(player, MessageUtils.buildHelpLink("help.wp.3", Command.PLAYER));
        sendMessage(player, "help.wp.5");
        sendMessage(player, new StringTextComponent(""));
    }

    public static void promptRegionFlags(PlayerEntity player, String regionName) {
        RegionManager.get().getRegion(regionName).ifPresent(region -> {
            // TODO: lang-key
            sendMessage(player, new TranslationTextComponent(TextFormatting.BOLD + "== Flags in Region '" + regionName + "' =="));
            if (region.getFlags().isEmpty()) {
                sendMessage(player, new TranslationTextComponent("message.region.info.noflags"));
                return;
            }
            region.getFlags().forEach(flag -> {
                sendMessage(player, MessageUtils.buildRemoveFlagLink(flag, regionName));
            });
            sendMessage(player, new StringTextComponent(""));
        });
    }

    public static IFormattableTextComponent buildRemoveFlagLink(String flag, String region) {
        String removeFlagCommand = "/" + WP_CMD + " " + Command.FLAG + " " + Command.REMOVE + " " + region + " " + flag;
        return new StringTextComponent(" - ")
                // TODO: lang-key
                .appendSibling(buildRunCommandLink("x", removeFlagCommand, TextFormatting.RED, "Remove flag '" + flag + "'"))
                .appendSibling(new StringTextComponent(" '" + flag + "'"));
    }

    public static IFormattableTextComponent buildHelpSuggestionLink(String translationKey, Command baseCmd, Command cmd) {
        String command = "/" + WP_CMD + " " + baseCmd + " " + cmd + " ";
        return new StringTextComponent(" ")
                .appendSibling(buildSuggestCommandLink("=>", command, TextFormatting.GREEN, "chat.link.hover.command.copy"))
                .appendSibling(new StringTextComponent(" "))
                .appendSibling(new TranslationTextComponent(translationKey));
    }

    public static IFormattableTextComponent buildHelpLink(String translationKey, Command cmd) {
        String command =  "/" + WP_CMD + " " + cmd.toString() + " " + Command.HELP;
        return new StringTextComponent(" ")
                .appendSibling(buildRunCommandLink("=>", command, TextFormatting.GREEN, "Show detailed help for the " + cmd.toString() + " commands"))
                .appendSibling(new StringTextComponent(" "))
                .appendSibling(new TranslationTextComponent(translationKey));
    }

    public static IFormattableTextComponent buildHelpHeader(String translationKey){
        return new StringTextComponent(TextFormatting.BOLD + " == ")
                .appendSibling(new TranslationTextComponent(translationKey).setStyle(Style.EMPTY.setBold(true)))
                .appendSibling(new StringTextComponent(TextFormatting.BOLD + " == "));
    }

    public static IFormattableTextComponent buildHelpHeader(TranslationTextComponent translationTextComponent){
        return new StringTextComponent(TextFormatting.BOLD + " == ")
                .appendSibling(translationTextComponent)
                .appendSibling(new StringTextComponent(TextFormatting.BOLD + " == "));
    }

    // TODO: add overloading with lang-key
    public static IFormattableTextComponent buildFlagListLink(IRegion region) {
        String command = "/" + WP_CMD + " " + Command.FLAG + " " + Command.LIST + " " + region.getName();
        return new StringTextComponent(" ")
                .appendSibling(buildRunCommandLink(region.getFlags().size() + " flag(s)", command,
                        TextFormatting.AQUA, "List flags in region '" + region.getName() + "'"));
    }

    // TODO: add overloading with lang-key
    public static IFormattableTextComponent buildAddFlagLink(String regionName) {
        String command =  "/" + WP_CMD + " " + Command.FLAG + " " + Command.ADD + " " + regionName + " ";
        return new StringTextComponent(" ").appendSibling(buildSuggestCommandLink("+", command,
                        TextFormatting.GREEN, "Add new flag to region '" + regionName + "'"));
    }

    // TODO: add overloading with lang-key
    public static IFormattableTextComponent buildPlayerListLink(IRegion region){
        String command = "/" + WP_CMD + " " + Command.PLAYER + " " + Command.LIST + " " + region.getName();
        return new StringTextComponent(" ")
                .appendSibling(buildRunCommandLink(region.getPlayers().size() + " player(s)", command,
                        TextFormatting.AQUA, "List players in region '" + region.getName() + "'"));
    }

    // TODO: add overloading with lang-key
    public static IFormattableTextComponent buildAddPlayerLink(String regionName){
        String command = "/" + WP_CMD + " " + Command.PLAYER + " " + Command.ADD + " " + regionName + " ";
        return new StringTextComponent(" ").appendSibling(buildSuggestCommandLink("+", command,
                        TextFormatting.GREEN, "Add new player to region '" + regionName + "'"));
    }

    // TODO: lang-keys
    public static IFormattableTextComponent buildRegionPriorityInfoLink(String regionName, int regionPriority) {
        String baseCommand = "/" + WP_CMD + " " + Command.REGION + " " + Command.SET_PRIORITY + " " + regionName + " ";
        String setCommand = baseCommand + regionPriority;
        String incrementCommand = baseCommand + (regionPriority + DEFAULT_REGION_PRIORITY_INC.get());
        String decrementCommand = baseCommand + (regionPriority - DEFAULT_REGION_PRIORITY_INC.get());
        return new TranslationTextComponent("message.region.info.priority", regionPriority)
                .appendSibling(new StringTextComponent(" "))
                .appendSibling(buildSuggestCommandLink("#", setCommand,
                        TextFormatting.GREEN, "Set new priority for region '" + regionName + "'"))
                .appendSibling(new StringTextComponent(" "))
                .appendSibling(buildRunCommandLink("+", incrementCommand,
                        TextFormatting.GREEN, "Increment region priority by " + DEFAULT_REGION_PRIORITY_INC.get()))
                .appendSibling(new StringTextComponent(" "))
                .appendSibling(buildRunCommandLink("-", decrementCommand,
                        TextFormatting.RED, "Decrement region priority by " + DEFAULT_REGION_PRIORITY_INC.get()));
    }

    // TODO: lang key
    public static IFormattableTextComponent buildRegionInfoLink(String regionName){
        String command =  "/" + WP_CMD + " " + Command.REGION + " " + Command.INFO + " " + regionName;
        return buildRunCommandLink(regionName, command, TextFormatting.GREEN,
                "Show region info for region '" + regionName + "'");
    }

    public static IFormattableTextComponent buildRegionMuteLink(IRegion region){
        boolean isMuted = region.isMuted();
        IFormattableTextComponent linkText = isMuted
                ? new TranslationTextComponent("message.region.info.muted.true")
                : new TranslationTextComponent("message.region.info.muted.false");
        TextFormatting color = isMuted
                ? TextFormatting.RED
                : TextFormatting.GREEN;
        String onClickAction = isMuted ? "unmute" : "mute";
        String command = "/" + WP_CMD + " " + Command.REGION + " " + onClickAction + " " + region.getName();
        // TODO: translatable overload (linkText) ?
        return buildRunCommandLink(linkText.getString(), command, color, onClickAction + " " + Command.REGION + " '" + region.getName() + "'");
    }

    public static IFormattableTextComponent buildRegionActiveLink(IRegion region){
        boolean isActive = region.isActive();
        IFormattableTextComponent activeText = isActive
                ? new TranslationTextComponent("message.region.info.active.true")
                : new TranslationTextComponent("message.region.info.active.false");
        TextFormatting color = isActive
                ? TextFormatting.GREEN
                : TextFormatting.RED;
        String onClickAction = isActive ? "deactivate" : "activate";
        String command = "/" + WP_CMD + " " + Command.REGION + " " + onClickAction + " " + region.getName();
        // TODO: translatable overload (linkText) ?
        return buildRunCommandLink(activeText.getString(), command, color, onClickAction + " " + Command.REGION + " '" + region.getName() + "'");
    }
}
