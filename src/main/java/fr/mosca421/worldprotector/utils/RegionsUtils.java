package fr.mosca421.worldprotector.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Joiner;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class RegionsUtils {

	public static void teleportRegion(String regions, ServerPlayerEntity player) {
        UUID playerUUID = player.getUniqueID();
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			player.sendMessage(new TranslationTextComponent("message.region.teleport", regions), playerUUID);
			player.setPositionAndUpdate(region.getArea().minX, 200, region.getArea().minZ);
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions), playerUUID);
		}
	}

	public static void addPlayer(String regions, ServerPlayerEntity player, String name) {
        UUID playerUUID = player.getUniqueID();
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (!region.addPlayer(name))
				player.sendMessage(new TranslationTextComponent("message.region.errorplayer"), playerUUID);
			player.sendMessage(new TranslationTextComponent("message.region.addplayer"), playerUUID);
			Saver.save();
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions), playerUUID);
		}
	}

	public static void removePlayer(String regions, ServerPlayerEntity player, String name) {
        UUID playerUUID = player.getUniqueID();
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (!region.removePlayer(name))
				player.sendMessage(new TranslationTextComponent("message.region.unknownplayer"), playerUUID);

			player.sendMessage(new TranslationTextComponent("message.region.removeplayer"), playerUUID);
			Saver.save();
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions), playerUUID);
		}
	}

	public static void removeRegion(String regions, ServerPlayerEntity player) {
        UUID playerUUID = player.getUniqueID();
		if (Saver.REGIONS.remove(regions) != null) {
			player.sendMessage(new TranslationTextComponent("message.region.remove", regions), playerUUID);
			Saver.save();
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions), playerUUID);
		}
	}

	public static void createRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
        UUID playerUUID = player.getUniqueID();
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTag()) {
				if (item.getTag().getBoolean("valide")) {
					AxisAlignedBB regions = new AxisAlignedBB(item.getTag().getInt("x1"), item.getTag().getInt("y1"), item.getTag().getInt("z1"), item.getTag().getInt("x2"), item.getTag().getInt("y2"), item.getTag().getInt("z2")).grow(1);
					Region region = new Region(regionName, regions, player.world.getDimension().getType().getId());
					Saver.addRegion(region);
					Saver.save();
					player.sendMessage(new TranslationTextComponent("message.region.define", regionName), playerUUID);
				} else {
					player.sendMessage(new TranslationTextComponent("message.itemhand.choose"), playerUUID);
				}
			} else {
				player.sendMessage(new TranslationTextComponent("message.itemhand.choose"), playerUUID);
			}
		} else {
			player.sendMessage(new TranslationTextComponent("message.itemhand.take"), playerUUID);
		}
	}

	public static void giveHelpMessage(ServerPlayerEntity player) {
	    UUID playerUUID = player.getUniqueID();
		player.sendMessage(new TranslationTextComponent(""), playerUUID);
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.1"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.2"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.3"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.4"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.5"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.6"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.7"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.8"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.region.9"), playerUUID);
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="), playerUUID);

	}

	public static void giveRegionList(ServerPlayerEntity player) {
		player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Region : " + Joiner.on(", ").join(Saver.REGIONS.keySet())), player.getUniqueID());
	}

	public static void redefineRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
        UUID playerUUID = player.getUniqueID();
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTag()) {
				if (item.getTag().getBoolean("valide")) {
					AxisAlignedBB regions = new AxisAlignedBB(item.getTag().getInt("x1"), item.getTag().getInt("y1"), item.getTag().getInt("z1"), item.getTag().getInt("x2"), item.getTag().getInt("y2"), item.getTag().getInt("z2")).grow(1);
					Region region = new Region(regionName, regions, player.world.getDimension().getType().getId());
					if (Saver.REGIONS.containsKey(regionName)) {
						Region oldRegion = Saver.REGIONS.get(regionName);
						for (String st : oldRegion.getFlags()) {
							region.addFlag(st);
						}
						Saver.REGIONS.remove(regionName);
						Saver.addRegion(region);
						Saver.save();
						player.sendMessage(new TranslationTextComponent("message.region.redefine", regionName), playerUUID);
					} else {
						player.sendMessage(new TranslationTextComponent("message.region.unknown", regionName), playerUUID);
					}
				} else {
					player.sendMessage(new TranslationTextComponent("message.itemhand.choose"), playerUUID);
				}
			} else {
				player.sendMessage(new TranslationTextComponent("message.itemhand.choose"), playerUUID);
			}
		} else {
			player.sendMessage(new TranslationTextComponent("message.itemhand.take"), playerUUID);
		}
	}

	public static List<Region> getHandlingRegionsFor(BlockPos position, int dimension) {
		int maxPriority = 1;
		ArrayList<Region> handlers = new ArrayList<Region>();
		for (Region region : Saver.REGIONS.values()) {
			// Changed: new Vec3d(position) -> new Vector3d(position.getX(), position.getY(), position.getZ()))
			if (region.getDimension() == dimension && region.getArea().contains(new Vector3d(position.getX(), position.getY(), position.getZ()))) {
				if (region.getPriority() == maxPriority) {
					handlers.add(region);
				} else if (region.getPriority() > maxPriority) {
					handlers.clear();
					maxPriority = region.getPriority();
					handlers.add(region);
				}
			}
		}
		return handlers;
	}

	public static void setPriorityRegion(String regionName, int priority, ServerPlayerEntity player) {
        UUID playerUUID = player.getUniqueID();
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			if (priority >= 1) {
				region.setPriority(priority);
				player.sendMessage(new TranslationTextComponent("message.region.setpriority", priority, regionName), playerUUID);
			} else {
				player.sendMessage(new TranslationTextComponent("message.region.priority"), playerUUID);
			}
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regionName), playerUUID);
		}
	}

	public static void getPriority(String regionName, ServerPlayerEntity player) {
        UUID playerUUID = player.getUniqueID();
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			String priority = "" + region.getPriority();
			player.sendMessage(new TranslationTextComponent("message.region.infopriority", regionName, priority), playerUUID);
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regionName), playerUUID);
		}
	}

	public static boolean isInRegion(String regionName, ServerPlayerEntity player) {
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			if (region.isInPlayerList(player)) {
				return true;
			}
			return false;
		}
		return false;
	}
}
