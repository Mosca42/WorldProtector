package fr.mosca421.worldprotector.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import fr.mosca421.worldprotector.core.AxisRegions;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class RegionsUtils {

	public static void teleportRegion(String regions, EntityPlayerMP player) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			player.sendMessage(new TextComponentTranslation("message.region.teleport", regions));
			player.setPositionAndUpdate(region.getArea().minX, 200, region.getArea().minZ);
		} else {
			player.sendMessage(new TextComponentTranslation("message.region.unknown", regions));
		}
	}

	public static void removeRegion(String regions, EntityPlayerMP player) {
		if (Saver.REGIONS.remove(regions) != null) {
			player.sendMessage(new TextComponentTranslation("message.region.remove", regions));
			Saver.save();
		} else {
			player.sendMessage(new TextComponentTranslation("message.region.unknown", regions));
		}
	}

	public static void createRegion(String regionName, EntityPlayerMP player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTagCompound()) {
				if (item.getTagCompound().getBoolean("valide")) {
					AxisRegions regions = new AxisRegions(item.getTagCompound().getInteger("x1"), item.getTagCompound().getInteger("y1"), item.getTagCompound().getInteger("z1"), item.getTagCompound().getInteger("x2"), item.getTagCompound().getInteger("y2"), item.getTagCompound().getInteger("z2")).expandXyz(1);
					Region region = new Region(regionName, regions, player.world.provider.getDimension());
					Saver.addRegion(region);
					Saver.save();
					player.sendMessage(new TextComponentTranslation("message.region.define", regionName));
				} else {
					player.sendMessage(new TextComponentTranslation("message.itemhand.choose"));
				}
			} else {
				player.sendMessage(new TextComponentTranslation("message.itemhand.choose"));
			}
		} else {
			player.sendMessage(new TextComponentTranslation("message.itemhand.take"));
		}
	}

	public static void giveHelpMessage(EntityPlayerMP player) {
		player.sendMessage(new TextComponentTranslation(""));
		player.sendMessage(new TextComponentTranslation(TextFormatting.BLUE + "==WorldProtector Help=="));
		player.sendMessage(new TextComponentTranslation("help.region.1"));
		player.sendMessage(new TextComponentTranslation("help.region.2"));
		player.sendMessage(new TextComponentTranslation("help.region.3"));
		player.sendMessage(new TextComponentTranslation("help.region.4"));
		player.sendMessage(new TextComponentTranslation("help.region.5"));
		player.sendMessage(new TextComponentTranslation("help.region.6"));
		player.sendMessage(new TextComponentTranslation("help.region.7"));
		player.sendMessage(new TextComponentTranslation(TextFormatting.BLUE + "==WorldProtector Help=="));

	}

	public static void giveRegionList(EntityPlayerMP player) {
		player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Flags : " + Joiner.on(", ").join(Saver.REGIONS.keySet())));
	}

	public static void redefineRegion(String regionName, EntityPlayerMP player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTagCompound()) {
				if (item.getTagCompound().getBoolean("valide")) {
					AxisRegions regions = new AxisRegions(item.getTagCompound().getInteger("x1"), item.getTagCompound().getInteger("y1"), item.getTagCompound().getInteger("z1"), item.getTagCompound().getInteger("x2"), item.getTagCompound().getInteger("y2"), item.getTagCompound().getInteger("z2")).expandXyz(1);
					Region region = new Region(regionName, regions, player.world.provider.getDimension());
					if (Saver.REGIONS.containsKey(regionName)) {
						Region oldRegion = Saver.REGIONS.get(regionName);
						for (String st : oldRegion.getFlags()) {
							region.addFlag(st);
						}
						Saver.REGIONS.remove(regionName);
						Saver.addRegion(region);
						Saver.save();
						player.sendMessage(new TextComponentTranslation("message.region.redefine", regionName));
					} else {
						player.sendMessage(new TextComponentTranslation("message.region.unknown", regionName));
					}
				} else {
					player.sendMessage(new TextComponentTranslation("message.itemhand.choose"));
				}
			} else {
				player.sendMessage(new TextComponentTranslation("message.itemhand.choose"));
			}
		} else {
			player.sendMessage(new TextComponentTranslation("message.itemhand.take"));
		}
	}

	public static List<Region> getHandlingRegionsFor(BlockPos position, int dimension) {
		int maxPriority = 1;
		ArrayList<Region> handlers = new ArrayList<Region>();
		for (Region region : Saver.REGIONS.values()) {
			if (region.getDimension() == dimension && region.getArea().isVecInside(new Vec3d(position))) {
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

	public static void setPriorityRegion(String regionName, int priority, EntityPlayerMP player) {
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			if (priority >= 1) {
				region.setPriority(priority);
				player.sendMessage(new TextComponentTranslation("message.region.setpriority", priority, regionName));
			} else {
				player.sendMessage(new TextComponentTranslation("message.region.priority"));
			}
		} else {
			player.sendMessage(new TextComponentTranslation("message.region.unknown", regionName));
		}
	}

	public static void getPriority(String regionName, EntityPlayerMP player) {
		if (Saver.REGIONS.containsKey(regionName)) {
			Region region = Saver.REGIONS.get(regionName);
			String priority = "" + region.getPriority();
			player.sendMessage(new TextComponentTranslation("message.region.infopriority", regionName, priority));
		} else {
			player.sendMessage(new TextComponentTranslation("message.region.unknown", regionName));
		}
	}
}
