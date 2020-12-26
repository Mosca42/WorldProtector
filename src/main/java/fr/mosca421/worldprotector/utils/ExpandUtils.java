package fr.mosca421.worldprotector.utils;

import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class ExpandUtils {
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		UUID playerUUID = player.getUniqueID();
		player.sendMessage(new StringTextComponent(""), playerUUID);
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.expand.1"), playerUUID);
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="), playerUUID);
	}

	public static void expandVert(ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTag()) {
				if (item.getTag().getBoolean("valide")) {
					item.getTag().putDouble("y1", 0);
					item.getTag().putDouble("y2", 255);
					player.sendStatusMessage(new TranslationTextComponent("message.itemhand.expand"), true);
				} else {

					player.sendMessage(new TranslationTextComponent("message.itemhand.choose"), player.getUniqueID());
				}
			} else {
				player.sendMessage(new TranslationTextComponent("message.itemhand.choose"), player.getUniqueID());
			}
		} else {
			player.sendMessage(new TranslationTextComponent("message.itemhand.take"), player.getUniqueID());
		}
	}
}
