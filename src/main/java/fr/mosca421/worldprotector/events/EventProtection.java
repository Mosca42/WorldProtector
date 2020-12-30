package fr.mosca421.worldprotector.events;

import java.util.Iterator;
import java.util.List;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
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

	private EventProtection(){}

	@SubscribeEvent
	public static void onPlayerBreakBlock(BreakEvent event) {
		PlayerEntity player = event.getPlayer();
		List<Region> regions = RegionsUtils.getHandlingRegionsFor(event.getPos(), RegionsUtils.getDimension(player.world));
		for (Region region : regions) {
			if (region.getFlags().contains(RegionFlag.BREAK.toString()) && !region.permits(player)) {
				player.sendMessage(new TranslationTextComponent("world.protection.break"), player.getUniqueID());
				event.setCanceled(true);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerPlaceBlock(EntityPlaceEvent event) {
		List<Region> regions = RegionsUtils.getHandlingRegionsFor(event.getPos(), RegionsUtils.getDimension((World) event.getWorld()));
		for (Region region : regions) {
			if (region.getFlags().contains(RegionFlag.PLACE.toString())) {
				if (event.getEntity() instanceof PlayerEntity) {
						if (!region.permits(((PlayerEntity)event.getEntity()))) {
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
				if (region.getFlags().contains(RegionFlag.EXPLOSION.toString())) {
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
					if (region.getFlags().contains(RegionFlag.EXPLOSION_OTHER.toString())) {
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
					if (region.getFlags().contains(RegionFlag.EXPLOSION_CREEPER.toString())) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBucketFill(FillBucketEvent event) {
		PlayerEntity player = event.getPlayer();
		if (event.getTarget() != null) {
			List<Region> regions = RegionsUtils.getHandlingRegionsFor(new BlockPos(event.getTarget().getHitVec()), RegionsUtils.getDimension(event.getWorld()));
			for (Region region : regions) {
				if (region.getFlags().contains(RegionFlag.PLACE.toString()) && !region.permits(player)) {
					player.sendMessage(new TranslationTextComponent("world.protection.place"), player.getUniqueID());
					event.setCanceled(true);
					return;
				}
			}
		}
	}
}
