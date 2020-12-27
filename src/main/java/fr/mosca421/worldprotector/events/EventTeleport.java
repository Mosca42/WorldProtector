package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)

public class EventTeleport {

	@SubscribeEvent
	public static void enderTeleport(EnderTeleportEvent event) {
		if (event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
			List<Region> regions = RegionsUtils.getHandlingRegionsFor(player.getPosition(), RegionsUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.getFlags().contains("enderpearls")) {
					if (!region.isInPlayerList(player)) {
						event.setCanceled(true);
						player.sendMessage(new TranslationTextComponent("world.ender.player"), player.getUniqueID());
					}
				}
			}
		}
	}
}
