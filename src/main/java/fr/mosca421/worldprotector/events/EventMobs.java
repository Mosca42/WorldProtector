package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)

public class EventMobs {

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity eventEntity = event.getEntity();
		for (Region region : Saver.REGIONS.values()) {
			if (region.getArea().contains(new Vec3d(eventEntity.getPosition()))) {
				if (region.getFlags().contains("mob-spawning-all")) {
					if (eventEntity instanceof MobEntity)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("mob-spawning-animals")) {
					if (eventEntity instanceof AnimalEntity || eventEntity instanceof WaterMobEntity)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("mob-spawning-monsters")) {
					if (eventEntity instanceof MonsterEntity
							|| eventEntity instanceof SlimeEntity
							|| eventEntity instanceof FlyingEntity
							|| eventEntity instanceof EnderDragonEntity
							|| eventEntity instanceof ShulkerEntity)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("exp-drop")) {
					if (eventEntity instanceof ExperienceOrbEntity) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onAttackEntityAnimal(AttackEntityEvent event) {
		if (!event.getTarget().world.isRemote) {
			if (event.getTarget() instanceof AnimalEntity) {
				AnimalEntity animal = (AnimalEntity) event.getTarget();
				for (Region region : Saver.REGIONS.values()) {
					if (region.getArea().contains(new Vec3d(animal.getPosition()))) {
						if (region.getFlags().contains("damage-animals")) {
							if (!region.isInPlayerList(event.getPlayer())) {
								event.getPlayer().sendMessage(new TranslationTextComponent("world.hurt.mob"));
								event.setCanceled(true);
							}
						}
					}
				}
			}

			if (event.getTarget() instanceof MobEntity) {
				MobEntity animal = (MobEntity) event.getTarget();
				for (Region region : Saver.REGIONS.values()) {
					if (region.getArea().contains(new Vec3d(animal.getPosition()))) {
						if (region.getFlags().contains("damage-monsters")) {
							if (!region.isInPlayerList(event.getPlayer())) {
								event.getPlayer().sendMessage(new TranslationTextComponent("world.hurt.mob"));
								event.setCanceled(true);
							}
						}
					}
				}
			}
		}
	}

}
