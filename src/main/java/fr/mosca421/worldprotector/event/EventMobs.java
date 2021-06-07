package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
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
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static fr.mosca421.worldprotector.util.RegionUtils.isPlayerActionProhibited;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventMobs {

	private EventMobs(){}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity eventEntity = event.getEntity();
		List<IRegion> affectedRegions = RegionUtils.getHandlingRegionsFor(event.getEntity().getPosition(), event.getWorld());
		for (IRegion region : affectedRegions) {
				if (region.containsFlag(RegionFlag.SPAWNING_ALL) && eventEntity instanceof MobEntity) {
					event.setCanceled(true);
					return;
				}
				if (region.containsFlag(RegionFlag.SPAWNING_ANIMAL) && isAnimal(eventEntity)) {
					event.setCanceled(true);
					return;
				}
				if (region.containsFlag(RegionFlag.SPAWNING_GOLEM) && eventEntity instanceof IronGolemEntity) {
					event.setCanceled(true);
					return;
				}
				if (region.containsFlag(RegionFlag.SPAWNING_MONSTERS) && isMonster(eventEntity)) {
					event.setCanceled(true);
					return;
				}
				if (region.containsFlag(RegionFlag.SPAWING_EXP) && eventEntity instanceof ExperienceOrbEntity) {
					event.setCanceled(true);
					return;
				}
		}
	}

	// TODO: Test on Villagers and add extra flag
	@SubscribeEvent
	public static void onBreedingAttempt(BabyEntitySpawnEvent event) {
		PlayerEntity player = event.getCausedByPlayer();
		if (player != null && !player.world.isRemote) {
			boolean isBreedingProhibited = isPlayerActionProhibited(event.getParentB().getPosition(), player, RegionFlag.ANIMAL_BREEDING);
			if (isBreedingProhibited) {
				MessageUtils.sendStatusMessage(player, "message.event.mobs.breed_animals");
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onAnimalTameAttempt(AnimalTameEvent event){
		AnimalEntity animal = event.getAnimal();
		PlayerEntity player = event.getTamer();
		if (!player.world.isRemote){
			boolean isTamingProhibited = isPlayerActionProhibited(animal.getPosition(), player, RegionFlag.ANIMAL_TAMING);
			if (isTamingProhibited) {
				event.setCanceled(true);
				MessageUtils.sendStatusMessage(player, "message.event.mobs.tame_animal");
			}
		}
	}

	public static boolean isAnimal(Entity entity){
		return entity instanceof AnimalEntity || entity instanceof WaterMobEntity;
	}

	public static boolean isMonster(Entity entity){
		return entity instanceof MonsterEntity
				|| entity instanceof SlimeEntity
				|| entity instanceof FlyingEntity
				|| entity instanceof EnderDragonEntity
				|| entity instanceof ShulkerEntity;
	}

	private static boolean regionContainsEntity(IRegion region, Entity entity){
		return region.getArea().contains(entity.getPositionVec());
	}

	@SubscribeEvent
	public static void onAttackEntityAnimal(AttackEntityEvent event) {
		PlayerEntity player = event.getPlayer();
		Entity eventEntity = event.getTarget();
		List<IRegion> affectedRegions = RegionUtils.getHandlingRegionsFor(event.getTarget().getPosition(), event.getTarget().world);
		if (!event.getTarget().world.isRemote) {
			if (isAnimal(eventEntity)) {
				for (IRegion region : affectedRegions) {
					boolean flagDamageAnimals = region.containsFlag(RegionFlag.DAMAGE_ANIMALS.toString());
					if (flagDamageAnimals && regionContainsEntity(region, eventEntity) && region.forbids(player)) {
						MessageUtils.sendStatusMessage(player, new TranslationTextComponent("message.event.mobs.hurt_animal"));
						event.setCanceled(true);
					}
				}
			}

			if (isMonster(eventEntity)) {
				for (IRegion region : affectedRegions) {
					boolean flagDamageMonsters = region.containsFlag(RegionFlag.DAMAGE_MONSTERS.toString());
					if (flagDamageMonsters && regionContainsEntity(region, eventEntity) && region.forbids(player)) {
						MessageUtils.sendStatusMessage(player, new TranslationTextComponent("message.event.mobs.hurt_monster"));
						event.setCanceled(true);
					}
				}
			}

			if (event.getTarget() instanceof VillagerEntity) { // exclude pesky wandering trader >:-)
				VillagerEntity villager = (VillagerEntity) event.getTarget();
				for (IRegion region : affectedRegions) {
					boolean flagDamageMonsters = region.containsFlag(RegionFlag.DAMAGE_VILLAGERS.toString());
					if (flagDamageMonsters && regionContainsEntity(region, villager) && region.forbids(player)) {
						MessageUtils.sendStatusMessage(player, new TranslationTextComponent("message.event.mobs.hurt_villager"));
						event.setCanceled(true);
					}
				}
			}
		}
	}

}
