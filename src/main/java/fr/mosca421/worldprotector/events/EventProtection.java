package fr.mosca421.worldprotector.events;

import java.util.Iterator;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventProtection {
	@SubscribeEvent
	public static void onPlayerBreakBlock(BreakEvent event) {
		int dim = event.getWorld().getDimension().getType().getId();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("break")) {
				if (!region.isInPlayerList(event.getPlayer())) {
					event.getPlayer().sendMessage(new TranslationTextComponent("world.protection.break"));
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerPlaceBlock(EntityPlaceEvent event) {
		int dim = event.getWorld().getDimension().getType().getId();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("place")) {
				if (event.getEntity() instanceof PlayerEntity) {
						if (!region.isInPlayerList(((PlayerEntity)event.getEntity()))) {
						((PlayerEntity)event.getEntity()).sendMessage(new TranslationTextComponent("world.protection.place"));
						event.setCanceled(true);
						return;
					}
				} else {
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
			for (Region region : RegionsUtils.getHandlingRegionsFor(pos, event.getWorld().getDimension().getType().getId())) {
				if (region.getFlags().contains("explosions")) {
					it.remove();
					break;
				}
			}
		}

		if (!(event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity)) {
			while (it.hasNext()) {
				BlockPos pos = it.next();
				for (Region region : RegionsUtils.getHandlingRegionsFor(pos, event.getWorld().getDimension().getType().getId())) {
					if (region.getFlags().contains("other-explosions")) {
						it.remove();
						break;
					}
				}
			}
		}

		if (event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity) {
			while (it.hasNext()) {
				BlockPos pos = it.next();
				for (Region region : RegionsUtils.getHandlingRegionsFor(pos, event.getWorld().getDimension().getType().getId())) {
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
		int dim = event.getWorld().getDimension().getType().getId();
		if (event.getTarget() != null) {
			for (Region region : RegionsUtils.getHandlingRegionsFor(new BlockPos(event.getTarget().getHitVec()), dim)) {
				if (region.getFlags().contains("place")) {
					if (!region.isInPlayerList(event.getPlayer())) {
						event.getPlayer().sendMessage(new TranslationTextComponent("world.protection.place"));
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
}
