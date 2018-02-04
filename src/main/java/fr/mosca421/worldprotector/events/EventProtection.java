package fr.mosca421.worldprotector.events;

import java.util.Iterator;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventProtection {

	@SubscribeEvent
	public static void onPlayerBreakBlock(BreakEvent event) {
		int dim = event.getWorld().provider.getDimension();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("break")) {
				if (!event.getPlayer().inventory.hasItemStack(new ItemStack(ItemsRegister.REGION_STICK))) {
					event.getPlayer().sendMessage(new TextComponentTranslation("world.protection.break"));
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerPlaceBlock(PlaceEvent event) {
		int dim = event.getWorld().provider.getDimension();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("place")) {
				if (!event.getPlayer().inventory.hasItemStack(new ItemStack(ItemsRegister.REGION_STICK))) {
					event.getPlayer().sendMessage(new TextComponentTranslation("world.protection.place"));
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		Iterator<BlockPos> it = event.getAffectedBlocks().iterator();
		while (it.hasNext()) {
			BlockPos pos = it.next();
			for (Region region : RegionsUtils.getHandlingRegionsFor(pos, event.getWorld().provider.getDimension())) {
				if (region.getFlags().contains("explosions")) {
					it.remove();
					break;
				}
			}
		}

		if (!(event.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper)) {
			while (it.hasNext()) {
				BlockPos pos = it.next();
				for (Region region : RegionsUtils.getHandlingRegionsFor(pos,
						event.getWorld().provider.getDimension())) {
					if (region.getFlags().contains("other-explosions")) {
						it.remove();
						break;
					}
				}
			}
		}

		if (event.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper) {
			while (it.hasNext()) {
				BlockPos pos = it.next();
				for (Region region : RegionsUtils.getHandlingRegionsFor(pos,
						event.getWorld().provider.getDimension())) {
					if (region.getFlags().contains("creeper-explosions")) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBucketFill(FillBucketEvent event) {
		int dim = event.getWorld().provider.getDimension();
		if (event.getTarget() != null) {
			for (Region region : RegionsUtils.getHandlingRegionsFor(event.getTarget().getBlockPos(), dim)) {
				if (region.getFlags().contains("place")) {
					if (!event.getEntityPlayer().inventory.hasItemStack(new ItemStack(ItemsRegister.REGION_STICK))) {
						event.getEntityPlayer().sendMessage(new TextComponentTranslation("world.protection.place"));
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
}
