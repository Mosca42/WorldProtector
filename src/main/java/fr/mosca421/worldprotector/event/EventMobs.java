package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.core.RegionSaver;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventMobs {

	private EventMobs(){}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity eventEntity = event.getEntity();
		for (Region region : RegionSaver.getRegions()) {
			if (regionContainsEntity(region, eventEntity)) {
				if (region.containsFlag(RegionFlag.SPAWNING_ALL.toString()) && eventEntity instanceof MobEntity) {
					event.setCanceled(true);
				}
				if (region.containsFlag(RegionFlag.SPAWNING_ANIMAL.toString()) && isAnimal(eventEntity)) {
					event.setCanceled(true);
				}

				if (region.containsFlag(RegionFlag.SPAWNING_MONSTERS.toString()) && isMonster(eventEntity)) {
					event.setCanceled(true);
				}
				if (region.containsFlag(RegionFlag.EXP_DROP.toString()) && eventEntity instanceof ExperienceOrbEntity) {
					event.setCanceled(true);
				}
			}
		}
	}

	private static boolean isAnimal(Entity entity){
		return entity instanceof AnimalEntity || entity instanceof WaterMobEntity;
	}

	private static boolean isMonster(Entity entity){
		return entity instanceof MonsterEntity
				|| entity instanceof SlimeEntity
				|| entity instanceof FlyingEntity
				|| entity instanceof EnderDragonEntity
				|| entity instanceof ShulkerEntity;
	}

	private static boolean regionContainsEntity(Region region, Entity entity){
		return region.getArea().contains(entity.getPositionVec());
	}

	@SubscribeEvent
	public static void onAttackEntityAnimal(AttackEntityEvent event) {
		PlayerEntity player = event.getPlayer();
		Entity eventEntity = event.getTarget();
		List<Region> affectedRegions = RegionUtils.getHandlingRegionsFor(event.getTarget().getPosition(), RegionUtils.getDimension(event.getTarget().world));
		if (!event.getTarget().world.isRemote) {
			if (isAnimal(eventEntity)) {
				for (Region region : affectedRegions) {
					boolean flagDamageAnimals = region.containsFlag(RegionFlag.DAMAGE_ANIMALS.toString());
					boolean isInPlayerList = RegionUtils.isInRegion(region.getName(), player);
					if (flagDamageAnimals && regionContainsEntity(region, eventEntity) && !isInPlayerList) {
						player.sendMessage(new TranslationTextComponent("world.hurt.animal"), player.getUniqueID());
						event.setCanceled(true);
					}
				}
			}

			if (isMonster(eventEntity)) {
				for (Region region : affectedRegions) {
					boolean flagDamageMonsters = region.containsFlag(RegionFlag.DAMAGE_MONSTERS.toString());
					boolean isInPlayerList = RegionUtils.isInRegion(region.getName(), player);
					if (flagDamageMonsters && regionContainsEntity(region, eventEntity) && !isInPlayerList) {
						player.sendMessage(new TranslationTextComponent("world.hurt.mob"), player.getUniqueID());
						event.setCanceled(true);
					}
				}
			}

			if (event.getTarget() instanceof VillagerEntity) { // exclude pesky wandering trader >:-)
				VillagerEntity villager = (VillagerEntity) event.getTarget();
				for (Region region : affectedRegions) {
					boolean flagDamageMonsters = region.containsFlag(RegionFlag.DAMAGE_VILLAGERS.toString());
					boolean isInPlayerList = RegionUtils.isInRegion(region.getName(), player);
					if (flagDamageMonsters && regionContainsEntity(region, villager) && !isInPlayerList) {
						player.sendMessage(new TranslationTextComponent("world.hurt.villager"), player.getUniqueID());
						event.setCanceled(true);
					}
				}
			}
		}
	}

}
