package fr.mosca421.worldprotector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.mosca421.worldprotector.commands.CommandsRegister;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WorldProtector.MODID)
public class WorldProtector {

	public static final String MODID = "worldprotector";

	public WorldProtector() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::isInRegion);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		System.out.println("fonctionne");
		CommandsRegister.init(event.getCommandDispatcher());
		Saver.onServerStarting(event);
	}

	public boolean enter = false;
	public String exitMessage = "";
	public String exitMessageSmall = "";

	@SubscribeEvent
	public void isInRegion(PlayerTickEvent event) {
		if (event.player instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.player;
			int dim = player.world.getDimension().getType().getId();
			for (Region region : RegionsUtils.getHandlingRegionsFor(player.getPosition(), dim)) {
				if (region.getFlags().contains("enter-message")) {
					try {
						if (!enter) {
							player.connection.sendPacket(new STitlePacket(STitlePacket.Type.SUBTITLE,
									TextComponentUtils.updateForEntity(player.getCommandSource(),
											new StringTextComponent(region.getEnterMessageSmall().replace("&", "§")),
											player, 0)));
							player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
									TextComponentUtils.updateForEntity(player.getCommandSource(),
											new StringTextComponent(region.getEnterMessage().replace("&", "§")), player,
											0),
									10, 10, 10));
							if (region.getFlags().contains("exit-message")) {
								exitMessage = region.getExitMessage();
								exitMessageSmall = region.getExitMessageSmall();
							} else {
								region.setExitMessage("");
								region.setExitMessageSmall("");
								exitMessage = "";
								exitMessageSmall = "";
							}
							enter = true;
						}
						return;
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
					}
				} else {
					region.setEnterMessage("");
					region.setEnterMessageSmall("");
				}
			}
			if (enter) {
				enter = false;
				if (!exitMessage.equals(""))
					try {
						player.connection.sendPacket(new STitlePacket(STitlePacket.Type.SUBTITLE,
								TextComponentUtils.updateForEntity(player.getCommandSource(),
										new StringTextComponent(exitMessageSmall.replace("&", "§")), player, 0)));
						player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
								TextComponentUtils.updateForEntity(player.getCommandSource(),
										new StringTextComponent(exitMessage.replace("&", "§")), player, 0),
								10, 10, 10));
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
					}
			}
		}
	}
}