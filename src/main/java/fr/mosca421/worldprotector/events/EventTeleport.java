package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventTeleport {

	@SubscribeEvent
	public static void enderTeleport(EnderTeleportEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
			int dim = player.world.provider.getDimension();
			for (Region region : RegionsUtils.getHandlingRegionsFor(player.getPosition(), dim)) {
				if (region.getFlags().contains("enderpearls")) {
					event.setCanceled(true);
					player.sendMessage(new TextComponentTranslation("world.ender.player"));
				}
			}
		}
	}
}
