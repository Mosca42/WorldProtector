package fr.mosca421.worldprotector.utils;

import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ExpandUtils {
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		player.sendMessage(new StringTextComponent(""));
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		player.sendMessage(new TranslationTextComponent("help.expand.1"));
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}

	public static void expandVert(ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTag()) {
				if (item.getTag().getBoolean("valide")) {
					item.getTag().putDouble("y1", 0);
					item.getTag().putDouble("y2", 255);
					player.sendStatusMessage(new TranslationTextComponent("message.itemhand.expand"), true);
				} else {
					player.sendMessage(new TranslationTextComponent("message.itemhand.choose"));
				}
			} else {
				player.sendMessage(new TranslationTextComponent("message.itemhand.choose"));
			}
		} else {
			player.sendMessage(new TranslationTextComponent("message.itemhand.take"));
		}
	}
}
