package fr.mosca421.worldprotector.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class MessageUtils {

    private MessageUtils(){}

    public static void sendMessage(PlayerEntity player, ITextComponent textComponent){
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public static void sendMessage(PlayerEntity player, String translationKey){
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUniqueID());
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }

    public static void sendTeleportLink(PlayerEntity player, BlockPos tpTarget, IFormattableTextComponent msg) {
        sendMessage(player, msg.append(TextComponentUtils.wrapWithSquareBrackets(new StringTextComponent(format(tpTarget.getX()) + ", " + format(tpTarget.getZ())))
                .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + tpTarget.getX() + " " + tpTarget.getY() + " " + tpTarget.getZ()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to region")))))
        );
    }

    public static void sendTeleportLink(PlayerEntity player, BlockPos tpTarget) {
        sendMessage(player, new StringTextComponent("").append(TextComponentUtils.wrapWithSquareBrackets(new StringTextComponent(format(tpTarget.getX()) + ", " + format(tpTarget.getZ())))
                .setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + tpTarget.getX() + " " + tpTarget.getY() + " " + tpTarget.getZ()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to region")))))
        );
    }
}
