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

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventMobs {

	private EventMobs(){}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity eventEntity = event.getEntity();
		for (Region region : RegionSaver.getRegions()) {
			if (region.getArea().contains(eventEntity.getPositionVec())) {
				boolean isAnimal = eventEntity instanceof AnimalEntity || eventEntity instanceof WaterMobEntity;
				boolean isMonster = eventEntity instanceof MonsterEntity
						|| eventEntity instanceof SlimeEntity
						|| eventEntity instanceof FlyingEntity
						|| eventEntity instanceof EnderDragonEntity
						|| eventEntity instanceof ShulkerEntity;
				if (region.getFlags().contains(RegionFlag.SPAWNING_ALL.toString()) && eventEntity instanceof MobEntity) {
					event.setCanceled(true);
				}
				if (region.getFlags().contains(RegionFlag.SPAWNING_ANIMAL.toString()) && isAnimal) {
					event.setCanceled(true);
				}
				if (region.getFlags().contains(RegionFlag.SPAWNING_MONSTERS.toString()) && isMonster) {
					event.setCanceled(true);
				}
				if (region.getFlags().contains(RegionFlag.EXP_DROP.toString()) && eventEntity instanceof ExperienceOrbEntity) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onAttackEntityAnimal(AttackEntityEvent event) {
		PlayerEntity player = event.getPlayer();
		if (!event.getTarget().world.isRemote) {
			if (event.getTarget() instanceof AnimalEntity) {
				AnimalEntity animal = (AnimalEntity) event.getTarget();
				for (Region region : RegionSaver.getRegions()) {
					boolean flagDamageAnimals = region.getFlags().contains(RegionFlag.DAMAGE_ANIMALS.toString());
					boolean isInPlayerList = RegionUtils.isInRegion(region.getName(), player);
					boolean animalIsInRegion = region.getArea().contains(animal.getPositionVec());
					if (animalIsInRegion && flagDamageAnimals && !isInPlayerList) {
						player.sendMessage(new TranslationTextComponent("world.hurt.mob"), player.getUniqueID());
						event.setCanceled(true);
					}
				}
			}

			if (event.getTarget() instanceof MobEntity) {
				MobEntity monster = (MobEntity) event.getTarget();
				for (Region region : RegionSaver.getRegions()) {
					boolean flagDamageMonsters = region.getFlags().contains(RegionFlag.DAMAGE_MONSTERS.toString());
					boolean isInPlayerList = RegionUtils.isInRegion(region.getName(), player);
					boolean mobIsInRegion = region.getArea().contains(monster.getPositionVec());
					if (mobIsInRegion && flagDamageMonsters && !isInPlayerList) {
						player.sendMessage(new TranslationTextComponent("world.hurt.mob"), player.getUniqueID());
						event.setCanceled(true);

					}
				}
			}
		}
	}

}
