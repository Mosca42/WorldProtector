package fr.mosca421.worldprotector.events;

import java.util.Iterator;
import java.util.List;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventProtection {
	@SubscribeEvent
	public static void onPlayerBreakBlock(BreakEvent event) {
		List<Region> regions = RegionsUtils.getHandlingRegionsFor(event.getPos(), RegionsUtils.getDimension(event.getPlayer().world));
		for (Region region : regions) {
			if (region.getFlags().contains("break")) {
				if (!region.isInPlayerList(event.getPlayer())) {
					event.getPlayer().sendMessage(new TranslationTextComponent("world.protection.break"), event.getPlayer().getUniqueID());
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerPlaceBlock(EntityPlaceEvent event) {
		List<Region> regions = RegionsUtils.getHandlingRegionsFor(event.getPos(), RegionsUtils.getDimension((World) event.getWorld()));
		for (Region region : regions) {
			if (region.getFlags().contains("place")) {
				if (event.getEntity() instanceof PlayerEntity) {
						if (!region.isInPlayerList(((PlayerEntity)event.getEntity()))) {
						event.getEntity().sendMessage(new TranslationTextComponent("world.protection.place"), event.getEntity().getUniqueID());
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
			List<Region> regions = RegionsUtils.getHandlingRegionsFor(pos, RegionsUtils.getDimension(event.getWorld()));
			for (Region region : regions) {
				if (region.getFlags().contains("explosions")) {
					it.remove();
					break;
				}
			}
		}
		it = event.getAffectedBlocks().iterator();

		if (!(event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity)) {
			while (it.hasNext()) {
				BlockPos pos = it.next();
				List<Region> regions = RegionsUtils.getHandlingRegionsFor(pos, RegionsUtils.getDimension(event.getWorld()));
				for (Region region : regions) {
					if (region.getFlags().contains("other-explosions")) {
						it.remove();
						break;
					}
				}
			}
		}
		it = event.getAffectedBlocks().iterator();

		if (event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity) {
			while (it.hasNext()) {
				BlockPos pos = it.next();
				List<Region> regions = RegionsUtils.getHandlingRegionsFor(pos, RegionsUtils.getDimension(event.getWorld()));
				for (Region region : regions) {
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
		if (event.getTarget() != null) {
			List<Region> regions = RegionsUtils.getHandlingRegionsFor(new BlockPos(event.getTarget().getHitVec()), RegionsUtils.getDimension(event.getWorld()));
			for (Region region : regions) {
				if (region.getFlags().contains("place")) {
					if (!region.isInPlayerList(event.getPlayer())) {
						event.getPlayer().sendMessage(new TranslationTextComponent("world.protection.place"), event.getPlayer().getUniqueID());
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
}
