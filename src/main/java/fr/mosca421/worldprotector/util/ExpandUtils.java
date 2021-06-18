package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.item.ItemRegionMarker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import static fr.mosca421.worldprotector.item.ItemRegionMarker.*;
import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public final class ExpandUtils {

	private ExpandUtils() {
	}

	public static void giveHelpMessage(PlayerEntity player) {
		sendMessage(player, new TranslationTextComponent(TextFormatting.AQUA + "== WorldProtector Help =="));
		sendMessage(player, "help.expand.1");
		sendMessage(player, "help.expand.2");
	}

	public static void expandVert(PlayerEntity player, ItemStack item, int y1, int y2) {
		if (isValidRegionMarker(item)) {
			CompoundNBT itemTag = item.getTag();
			if (itemTag.getBoolean(VALID)) {
				itemTag.putDouble(Y1, y1);
				itemTag.putDouble(Y2, y2);
				sendMessage(player, new TranslationTextComponent("message.item-hand.expand", y1, y2));
			} else {
				sendMessage(player, "message.item-hand.choose");
			}
		} else {
			sendMessage(player, "message.item-hand.take");
		}
	}

	private static boolean isValidRegionMarker(ItemStack itemStack) {
		return itemStack.getItem() instanceof ItemRegionMarker && itemStack.hasTag();
	}

	public static void setDefaultYLevels(PlayerEntity player, int yLow, int yHigh) {
		ItemStack itemInMainHand = player.getHeldItemMainhand();
		if (isValidRegionMarker(itemInMainHand)) {
			ItemRegionMarker regionMarker = (ItemRegionMarker) itemInMainHand.getItem();
			regionMarker.setDefaultYValues(itemInMainHand, yLow, yHigh);
			sendMessage(player, new TranslationTextComponent("message.region-marker.set-y", yLow, yHigh));
		} else {
			sendMessage(player, "message.item-hand.take");
		}
	}
}
