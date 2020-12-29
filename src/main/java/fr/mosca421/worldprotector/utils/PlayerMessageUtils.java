package fr.mosca421.worldprotector.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerMessageUtils {

    private PlayerMessageUtils(){}

    public static void sendInfoMessage(ServerPlayerEntity player, String text){
        player.sendMessage(new TranslationTextComponent(text), player.getUniqueID());
    }

    public static void sendInfoMessage(ServerPlayerEntity player, ITextComponent textComponent){
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public static void sendInfoMessage(PlayerEntity player, ITextComponent textComponent){
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public static void sendInfoMessage(PlayerEntity player, String text){
        player.sendMessage(new TranslationTextComponent(text), player.getUniqueID());
    }
}
