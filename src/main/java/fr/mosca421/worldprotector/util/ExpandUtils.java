package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.item.RegionStick;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import static fr.mosca421.worldprotector.util.MessageUtils.*;

public class ExpandUtils {

	private ExpandUtils() {}
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		sendMessage(player, "");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		sendMessage(player,"help.expand.1");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}

	public static void expandVert(ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.getTag() != null) {
				CompoundNBT itemTag = item.getTag();
				if (item.hasTag()) {
					if (itemTag.getBoolean("valide")) {
						itemTag.putDouble("y1", 0);
						itemTag.putDouble("y2", 255);
						sendMessage(player, "message.itemhand.expand");
					} else {
						sendMessage(player, "message.itemhand.choose");
					}
				} else {
					sendMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendMessage(player, "message.itemhand.take");
		}
	}
}
