package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventInteract {

	@SubscribeEvent
	public static void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
		int dim = event.getWorld().provider.getDimension();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("use")) {
				if (!(event.getWorld().getTileEntity(event.getPos()) instanceof TileEntityLockable))
					if (!event.getEntityPlayer().inventory.hasItemStack(new ItemStack(ItemsRegister.REGION_STICK))) {
						event.setCanceled(true);
						event.getEntityPlayer().sendMessage(new TextComponentTranslation("world.interact.use"));
						return;
					}
			}
			if (region.getFlags().contains("chest-access")) {
				if (!event.getEntityPlayer().inventory.hasItemStack(new ItemStack(ItemsRegister.REGION_STICK))) {
					event.setCanceled(true);
					event.getEntityPlayer().sendMessage(new TextComponentTranslation("world.interact.use"));
					return;
				}
			}
		}
	}

}
