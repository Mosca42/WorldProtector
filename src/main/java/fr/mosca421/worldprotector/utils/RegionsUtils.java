package fr.mosca421.worldprotector.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class RegionsUtils {

	public static void teleportRegion(String regions, ServerPlayerEntity player) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			player.sendMessage(new TranslationTextComponent("message.region.teleport", regions));
			player.setPositionAndUpdate(region.getArea().minX, 200, region.getArea().minZ);
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions));
		}
	}

	public static void addPlayer(String regions, ServerPlayerEntity player, String name) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (!region.addPlayer(name))
				player.sendMessage(new TranslationTextComponent("message.region.errorplayer"));
			player.sendMessage(new TranslationTextComponent("message.region.addplayer"));
			Saver.save();
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions));
		}
	}

	public static void removePlayer(String regions, ServerPlayerEntity player, String name) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (!region.removePlayer(name))
				player.sendMessage(new TranslationTextComponent("message.region.unknownplayer"));

			player.sendMessage(new TranslationTextComponent("message.region.removeplayer"));
			Saver.save();
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions));
		}
	}

	public static void removeRegion(String regions, ServerPlayerEntity player) {
		if (Saver.REGIONS.remove(regions) != null) {
			player.sendMessage(new TranslationTextComponent("message.region.remove", regions));
			Saver.save();
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regions));
		}
	}

	public static void createRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTag()) {
				if (item.getTag().getBoolean("valide")) {
					AxisAlignedBB regions = new AxisAlignedBB(item.getTag().getInt("x1"), item.getTag().getInt("y1"), item.getTag().getInt("z1"), item.getTag().getInt("x2"), item.getTag().getInt("y2"), item.getTag().getInt("z2")).grow(1);
					Region region = new Region(regionName, regions, player.world.getDimension().getType().getId());
					Saver.addRegion(region);
					Saver.save();
					player.sendMessage(new TranslationTextComponent("message.region.define", regionName));
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

	public static void giveHelpMessage(ServerPlayerEntity player) {
		player.sendMessage(new TranslationTextComponent(""));
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		player.sendMessage(new TranslationTextComponent("help.region.1"));
		player.sendMessage(new TranslationTextComponent("help.region.2"));
		player.sendMessage(new TranslationTextComponent("help.region.3"));
		player.sendMessage(new TranslationTextComponent("help.region.4"));
		player.sendMessage(new TranslationTextComponent("help.region.5"));
		player.sendMessage(new TranslationTextComponent("help.region.6"));
		player.sendMessage(new TranslationTextComponent("help.region.7"));
		player.sendMessage(new TranslationTextComponent("help.region.8"));
		player.sendMessage(new TranslationTextComponent("help.region.9"));
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));

	}

	public static void giveRegionList(ServerPlayerEntity player) {
		player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Region : " + Joiner.on(", ").join(Saver.REGIONS.keySet())));
	}

	public static void redefineRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
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
						player.sendMessage(new TranslationTextComponent("message.region.redefine", regionName));
					} else {
						player.sendMessage(new TranslationTextComponent("message.region.unknown", regionName));
					}
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

	public static List<Region> getHandlingRegionsFor(BlockPos position, int dimension) {
		int maxPriority = 1;
		ArrayList<Region> handlers = new ArrayList<Region>();
		for (Region region : Saver.REGIONS.values()) {
			if (region.getDimension() == dimension && region.getArea().contains(new Vec3d(position))) {
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
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			if (priority >= 1) {
				region.setPriority(priority);
				player.sendMessage(new TranslationTextComponent("message.region.setpriority", priority, regionName));
			} else {
				player.sendMessage(new TranslationTextComponent("message.region.priority"));
			}
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void getPriority(String regionName, ServerPlayerEntity player) {
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			String priority = "" + region.getPriority();
			player.sendMessage(new TranslationTextComponent("message.region.infopriority", regionName, priority));
		} else {
			player.sendMessage(new TranslationTextComponent("message.region.unknown", regionName));
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
