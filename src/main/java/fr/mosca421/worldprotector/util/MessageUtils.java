package fr.mosca421.worldprotector.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageUtils {

    private MessageUtils(){}

    public static void sendMessage(ServerPlayerEntity player, String translationKey){
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUniqueID());
    }

    public static void sendMessage(ServerPlayerEntity player, ITextComponent textComponent){
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public static void sendMessage(PlayerEntity player, ITextComponent textComponent){
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public static void sendMessage(PlayerEntity player, String translationKey){
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUniqueID());
    }
}
