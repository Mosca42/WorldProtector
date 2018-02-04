package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

@EventBusSubscriber
public class EventPlayers {

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent event) {
		if (event.getTarget() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getTarget();
			int dim = player.world.provider.getDimension();
			for (Region region : RegionsUtils.getHandlingRegionsFor(player.getPosition(), dim)) {
				if (region.getFlags().contains("damage-players")) {
					event.getEntityPlayer().sendMessage(new TextComponentTranslation("world.pvp.player"));
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPickupItem(ItemPickupEvent event) {
		int dim = event.player.world.provider.getDimension();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.player.getPosition(), dim)) {
			if (region.getFlags().contains("pickup-item")) {
				event.player.sendMessage(new TextComponentTranslation("world.pickup.player"));
				event.setCanceled(true);
			}

		}
	}

	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
			int dim = player.world.provider.getDimension();
			for (Region region : RegionsUtils.getHandlingRegionsFor(player.getPosition(), dim)) {
				if (region.getFlags().contains("invincible")) {
					event.setCanceled(true);
					return;
				}

			}
		}
	}

	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
			int dim = player.world.provider.getDimension();
			for (Region region : RegionsUtils.getHandlingRegionsFor(player.getPosition(), dim)) {
				if (region.getFlags().contains("fall-damage")) {
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onSendChat(ServerChatEvent event) {
		if (event.getPlayer() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
			int dim = player.world.provider.getDimension();
			for (Region region : RegionsUtils.getHandlingRegionsFor(player.getPosition(), dim)) {
				if (region.getFlags().contains("send-chat")) {
					if (!player.inventory.hasItemStack(new ItemStack(ItemsRegister.REGION_STICK))) {
						event.setCanceled(true);
						event.getPlayer().sendMessage(new TextComponentTranslation("world.speak.player"));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerDropItem(ItemTossEvent event) {
		int dim = event.getPlayer().world.provider.getDimension();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPlayer().getPosition(), dim)) {
			if (region.getFlags().contains("item-drop")) {
				event.setCanceled(true);
				event.getPlayer().inventory.addItemStackToInventory(event.getEntityItem().getItem());
				event.getPlayer().sendMessage(new TextComponentTranslation("world.drop.player"));
			}
		}
	}
}
