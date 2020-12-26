package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)

public class EventMobs {

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		for (Region region : Saver.REGIONS.values()) {
			if (region.getArea().contains(event.getEntity().getPositionVec())) {
				if (region.getFlags().contains("mob-spawning-all")) {
					if (event.getEntity() instanceof MobEntity || event.getEntity() instanceof AnimalEntity || event.getEntity() instanceof WaterMobEntity || event.getEntity() instanceof SlimeEntity)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("mob-spawning-animals")) {
					if (event.getEntity() instanceof AnimalEntity || event.getEntity() instanceof WaterMobEntity)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("mob-spawning-monsters")) {
					if (event.getEntity() instanceof MobEntity || event.getEntity() instanceof SlimeEntity)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("exp-drop")) {
					if (event.getEntity() instanceof ExperienceOrbEntity) {
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
					if (region.getArea().contains(animal.getPositionVec())) {
						if (region.getFlags().contains("damage-animals")) {
							if (!region.isInPlayerList(event.getPlayer())) {
								event.getPlayer().sendMessage(new TranslationTextComponent("world.hurt.mob"), event.getPlayer().getUniqueID());
								event.setCanceled(true);
							}
						}
					}
				}
			}

			if (event.getTarget() instanceof MobEntity) {
				MobEntity animal = (MobEntity) event.getTarget();
				for (Region region : Saver.REGIONS.values()) {
					if (region.getArea().contains(animal.getPositionVec())) {
						if (region.getFlags().contains("damage-monsters")) {
							if (!region.isInPlayerList(event.getPlayer())) {
								event.getPlayer().sendMessage(new TranslationTextComponent("world.hurt.mob"), event.getPlayer().getUniqueID());
								event.setCanceled(true);
							}
						}
					}
				}
			}
		}
	}

}
