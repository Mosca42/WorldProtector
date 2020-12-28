package fr.mosca421.worldprotector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.commands.CommandsRegister;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(WorldProtector.MODID)
public class WorldProtector {

	public static final String MODID = "worldprotector";
	private static final Logger LOGGER = LogManager.getLogger();

	public WorldProtector() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::isInRegion);
		MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemsRegister.ITEMS.register(modEventBus);

	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandsRegister.init(event.getServer().getCommandManager().getDispatcher());
		Saver.onServerStarting(event);
	}

	/*
	private boolean enter = false;
	private String exitMessage = "";
	private String exitMessageSmall = "";
	*/

	// FIXME: isInRegion works not as intended - the flags only work for one region at a time.
	// if mupltiple regions are overlapping only 1 enter and exit message is displayer.
	// the flag for entering and leaving has to be saved per region

	@SubscribeEvent
	public void isInRegion(PlayerTickEvent event) {
		/*
		if (event.player instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.player;
			List<Region> regions = RegionsUtils.getHandlingRegionsFor(player.getPosition(), RegionsUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.getFlags().contains("enter-message")) {
					try {
						if (!enter) {
							player.connection.sendPacket(new STitlePacket(STitlePacket.Type.SUBTITLE,
									// Changed: .updateForEntity -> func_240645_a_
									TextComponentUtils.func_240645_a_(player.getCommandSource(),
											new StringTextComponent(region.getEnterMessageSmall().replace("&", "§")),
											player, 0)));
							player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
									// Changed: .updateForEntity -> func_240645_a_
									TextComponentUtils.func_240645_a_(player.getCommandSource(),
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
								// Changed: .updateForEntity -> func_240645_a_
								TextComponentUtils.func_240645_a_(player.getCommandSource(),
										new StringTextComponent(exitMessageSmall.replace("&", "§")), player, 0)));
						player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
								// Changed: .updateForEntity -> func_240645_a_
								TextComponentUtils.func_240645_a_(player.getCommandSource(),
										new StringTextComponent(exitMessage.replace("&", "§")), player, 0),
								10, 10, 10));
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
					}
			}
		}
		*/
	}
}