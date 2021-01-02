package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)

public class EventTeleport {

	private EventTeleport(){}

	@SubscribeEvent
	// TODO: maybe better item use and check if it is enderpearl and then cancel it
	public static void onEnderTeleport(EnderTeleportEvent event) {
		if (event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
			List<Region> regions = RegionUtils.getHandlingRegionsFor(player.getPosition(), RegionUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.ENDERPEARL_TELEPORTATION.toString()) && !region.permits(player)) {
					event.setCanceled(true);
					// refund enderpearl
					int count = player.getHeldItem(player.getActiveHand()).getCount();
					player.getHeldItem(player.getActiveHand()).setCount(count + 1);
					player.sendMessage(new TranslationTextComponent("world.ender.player"), player.getUniqueID());
				}
			}
		}
	}
}
