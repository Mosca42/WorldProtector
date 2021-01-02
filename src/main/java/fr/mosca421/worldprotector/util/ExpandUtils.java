package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.item.ItemRegionStick;
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

	public static void expandVert(ServerPlayerEntity player, ItemStack item, int y1, int y2) {
		if (y1 > y2) {
			sendMessage(player, "help.expand.error");
			return;
		}
		if (item.getItem() instanceof ItemRegionStick) {
			if (item.getTag() != null) {
				CompoundNBT itemTag = item.getTag();
				if (item.hasTag() && itemTag.getBoolean("valide")) {
					itemTag.putDouble("y1", y1);
					itemTag.putDouble("y2", y2);
					sendMessage(player, new TranslationTextComponent("message.itemhand.expand", y1, y2));
				} else {
					sendMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendMessage(player, "message.itemhand.take");
		}
	}
}
