package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.item.ItemRegionMarker;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionFlagUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventPlayers {

	private EventPlayers(){}

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent event) {
		if (!event.getPlayer().world.isRemote) {
			if (event.getTarget() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getTarget();
				List<Region> regions = RegionUtils.getHandlingRegionsFor(player.getPosition(), RegionUtils.getDimension(player.world));
				for (Region region : regions) {
					if (region.containsFlag(RegionFlag.DAMAGE_PLAYERS.toString()) && !RegionFlagUtils.isOp(player)) {
						event.getPlayer().sendMessage(new TranslationTextComponent("world.pvp.player"), event.getPlayer().getUniqueID());
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	// Use EntityItemPickupEvent instead
	public static void onPickupItem(ItemPickupEvent event) {
		List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPlayer().getPosition(), RegionUtils.getDimension(event.getPlayer().world));
		for (Region region : regions) {
			if (region.containsFlag(RegionFlag.ITEM_PICKUP.toString()) && !RegionUtils.isInRegion(region.getName(), event.getPlayer())) {
				event.getPlayer().sendMessage(new TranslationTextComponent("world.pickup.player"), event.getPlayer().getUniqueID());
				// event.setCanceled(true);
			}
		}
	}


	@SubscribeEvent
	public static void onPickupItem(EntityItemPickupEvent event) {
		if (!event.getPlayer().world.isRemote) {
			List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPlayer().getPosition(), RegionUtils.getDimension(event.getPlayer().world));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.ITEM_PICKUP.toString()) && !RegionUtils.isInRegion(region.getName(), event.getPlayer())) {
					// event.getPlayer().sendMessage(new TranslationTextComponent("world.pickup.player"), event.getPlayer().getUniqueID());
					// sendStatusMessage used here to not spam the user with messages while standing in the entityitem
					event.getPlayer().sendStatusMessage(new TranslationTextComponent("world.pickup.player"), true);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLevelChange(PlayerXpEvent.LevelChange event){
		if (!event.getPlayer().world.isRemote) {
			PlayerEntity player = event.getPlayer();
			boolean isLevelChangeProhibithed = RegionUtils.isPlayerActionProhibited(event.getPlayer().getPosition(), player, RegionFlag.LEVEL_CHANGE);
			if (isLevelChangeProhibithed) {
				event.setCanceled(true);
				MessageUtils.sendMessage(player, "message.event.player.level_change");
			}
		}
	}

	/*
	@SubscribeEvent
	public static void onPlayerXPChange(PlayerXpEvent.XpChange event){
		if (!event.getPlayer().world.isRemote) {
			PlayerEntity player = event.getPlayer();
			boolean isXpChangeProhibithed = RegionUtils.isPlayerActionProhibited(event.getPlayer().getPosition(), player, RegionFlag.EXP_CHANGE);
			if (isXpChangeProhibithed) {
				event.setCanceled(true);
				// TODO: Seems to not work as it should
				MessageUtils.sendMessage(player, "message.protection.player.level_change");
			}
		}
	}
	*/

	/*
	@SubscribeEvent
	public static void onPlayerXpPickup(PlayerXpEvent.PickupXp event){
		if (!event.getPlayer().world.isRemote) {
			PlayerEntity player = event.getPlayer();
			boolean isXpPickupProhibithed = RegionUtils.isPlayerActionProhibited(event.getPlayer().getPosition(), player, RegionFlag.EXP_PICKUP);
			if (isXpPickupProhibithed) {
				// TODO: Seems to not work as it should
				event.setCanceled(true);
				// MessageUtils.sendMessage(player, "message.protection.player.exp_change");
			}
		}
	}
	*/


	// replace with AttackEntityEvent for players and use LivingHurtEvent for enitites
	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
			List<Region> regions = RegionUtils.getHandlingRegionsFor(player.getPosition(), RegionUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.INVINCIBLE.toString())) {
					event.setCanceled(true);
					return;
				}

			}
		}
	}

	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		if (event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
			List<Region> regions = RegionUtils.getHandlingRegionsFor(player.getPosition(), RegionUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.FALL_DAMAGE.toString())) {
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onAnvilRepair(AnvilRepairEvent event) {
		if (!event.getPlayer().world.isRemote) {
			ItemStack rightIn = event.getIngredientInput();
			ItemStack leftIn = event.getItemInput();
			PlayerEntity player = event.getPlayer();
			if (leftIn.getItem() instanceof ItemRegionMarker && rightIn.getItem() instanceof AirItem) {
				// TODO: spaces are not allowed
				String regionName = event.getItemResult().getDisplayName().getString();
				if (!player.hasPermissionLevel(4) || !player.isCreative()) {
					sendMessage(player,   "message.region.define.denied");
				} else {
					RegionUtils.createRegion(regionName, player, event.getItemResult());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onSendChat(ServerChatEvent event) {
		if (event.getPlayer() != null) {
			ServerPlayerEntity player = event.getPlayer();
			List<Region> regions = RegionUtils.getHandlingRegionsFor(player.getPosition(), RegionUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.SEND_MESSAGE.toString()) && !RegionUtils.isInRegion(region.getName(), player)) {
					event.setCanceled(true);
					event.getPlayer().sendMessage(new TranslationTextComponent("world.speak.player"), event.getPlayer().getUniqueID());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerDropItem(ItemTossEvent event) {
		List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPlayer().getPosition(), RegionUtils.getDimension(event.getPlayer().world));
		for (Region region : regions) {
			if (region.containsFlag(RegionFlag.ITEM_DROP.toString()) && !RegionUtils.isInRegion(region.getName(), event.getPlayer())) {
				event.setCanceled(true);
				event.getPlayer().inventory.addItemStackToInventory(event.getEntityItem().getItem());
				event.getPlayer().sendMessage(new TranslationTextComponent("world.drop.player"), event.getPlayer().getUniqueID());
			}
		}
	}

	@SubscribeEvent
	public static void onEntityMountAttempt(EntityMountEvent event) {
		if (!event.getWorldObj().isRemote) {
			Entity entityBeingMounted = event.getEntityBeingMounted();
			// could be mob that dismounts because entity being mounted dies?
			boolean playerAttemptsMounting = event.getEntityMounting() instanceof PlayerEntity;
			if (playerAttemptsMounting) {
				PlayerEntity player = (PlayerEntity) event.getEntityMounting();
				List<Region> regions = RegionUtils.getHandlingRegionsFor(entityBeingMounted, event.getWorldObj());
				for (Region region : regions) {
					/*
					if (event.isDismounting() && region.containsFlag(RegionFlag.ANIMAL_UNMOUNTING) && region.forbids(player)) {
						event.setCanceled(true); // Does not correctly unmount player - TODO: report bug to forge?
						sendMessage(player, "message.event.player.unmount");
					}
					*/
					if (event.isMounting() && region.containsFlag(RegionFlag.ANIMAL_MOUNTING) && region.forbids(player)) {
						event.setCanceled(true);
						sendMessage(player, "message.event.player.mount");
					}
				}
			}
		}
	}
}
