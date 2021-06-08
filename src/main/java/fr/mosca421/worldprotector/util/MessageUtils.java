package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.core.IRegion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

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

    public static void sendDimensionTeleportLink(PlayerEntity player, IRegion region, IFormattableTextComponent msg) {
        BlockPos target = region.getTpTarget();
        String dim = region.getDimension().getLocation().toString();
        sendMessage(player, msg.append(TextComponentUtils.wrapWithSquareBrackets(new StringTextComponent(dim + "@ [" + target.getX() + ", " + target.getY() + ", " + target.getZ() + "]"))
                .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + dim + " run tp @s " + target.getX() + " " + target.getY() + " " + target.getZ()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to region")))))
        );
    }

    @Deprecated
    public static void sendTeleportLink(PlayerEntity player, BlockPos tpTarget, IFormattableTextComponent msg) {
        sendMessage(player, msg.append(TextComponentUtils.wrapWithSquareBrackets(new StringTextComponent(tpTarget.getX() + ", " + tpTarget.getZ()))
                .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + tpTarget.getX() + " " + tpTarget.getY() + " " + tpTarget.getZ()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to region")))))
        );
    }

    @Deprecated
    public static void sendTeleportLink(PlayerEntity player, BlockPos tpTarget) {
        sendMessage(player, new StringTextComponent("").append(TextComponentUtils.wrapWithSquareBrackets(new StringTextComponent(tpTarget.getX() + ", " + tpTarget.getZ()))
                .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + tpTarget.getX() + " " + tpTarget.getY() + " " + tpTarget.getZ()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to region")))))
        );
    }
}