package fr.mosca421.worldprotector.util;

import com.google.common.base.Joiner;
import fr.mosca421.worldprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public class RegionPlayerUtils {

    private RegionPlayerUtils() {}

    public static void addPlayers(String regionName, PlayerEntity sourcePlayer, List<PlayerEntity> playersToAdd){
        if (RegionManager.get().containsRegion(regionName)) {
            List<PlayerEntity> addedPlayers = RegionManager.get().addPlayers(regionName, playersToAdd);
            List<String> playerNames = addedPlayers.stream()
                    .map(player -> player.getName().getString())
                    .collect(Collectors.toList());
            String playerString = String.join(", ", playerNames);
            if (!addedPlayers.isEmpty()) {
                sendMessage(sourcePlayer, new TranslationTextComponent("message.players.add.multiple", playerString, regionName));
                addedPlayers.forEach(playerEntity -> sendMessage(playerEntity, new TranslationTextComponent( "message.player.regionadded", regionName)));
            } else {
                sendMessage(sourcePlayer, new TranslationTextComponent( "message.players.add.none", playerString));
            }
        } else {
            sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
        }
    }

    public static void addPlayer(String regionName, PlayerEntity sourcePlayer, PlayerEntity playerToAdd) {
        if (RegionManager.get().containsRegion(regionName)) {
            String playerToAddName = playerToAdd.getName().getString();
            if (RegionManager.get().addPlayer(regionName, playerToAdd)) {
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.addplayer", playerToAddName, regionName));
                sendMessage(playerToAdd, new TranslationTextComponent("message.player.regionadded", regionName));
            } else {
                // Player already defined in this region -> Message needed or silent acknowledgement?
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.errorplayer", regionName, playerToAddName));
            }
        } else {
            sendMessage(sourcePlayer,  new TranslationTextComponent("message.region.unknown", regionName));
        }
    }

    public static void removePlayer(String regionName, PlayerEntity sourcePlayer, PlayerEntity playerToRemove) {
        if (RegionManager.get().containsRegion(regionName)) {
            String playerToRemoveName = playerToRemove.getName().getString();
            if (RegionManager.get().removePlayer(regionName, playerToRemove)) {
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.removeplayer", playerToRemoveName, regionName));
                sendMessage(playerToRemove, new TranslationTextComponent("message.player.regionremoved", regionName));
            } else {
                // Player was not present in this region -> Message needed or silent acknowledgement?
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknownplayer", regionName, playerToRemoveName));
            }
        } else {
            sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
        }
    }

    public static void removePlayers(String regionName, PlayerEntity sourcePlayer, List<PlayerEntity> playersToRemove){
        if (RegionManager.get().containsRegion(regionName)) {
            List<PlayerEntity> removedPlayers = RegionManager.get().removePlayers(regionName, playersToRemove);
            List<String> playerNames = removedPlayers.stream()
                    .map(player -> player.getName().getString())
                    .collect(Collectors.toList());
            String playerString = String.join(", ", playerNames);
            if (!removedPlayers.isEmpty()) {
                sendMessage(sourcePlayer, new TranslationTextComponent("message.players.remove.multiple", playerString, regionName));
                removedPlayers.forEach(playerEntity -> sendMessage(playerEntity, new TranslationTextComponent( "message.player.regionremoved", regionName)));
            } else {
                sendMessage(sourcePlayer, new TranslationTextComponent( "message.players.remove.none", playerString));
            }
        } else {
            sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
        }
    }

    public static boolean isOp(PlayerEntity player) {
        OpEntry opPlayerEntry = ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .getOppedPlayers()
                .getEntry(player.getGameProfile());
        if (opPlayerEntry != null) {
            return opPlayerEntry.getPermissionLevel() == 4;
        }
        return false;
    }

    public static void listPlayersInRegion(String regionName, PlayerEntity player){
        Set<String> players = RegionManager.get().getRegionPlayers(regionName);
        if (!players.isEmpty()) {
            String playerString = Joiner.on(", ").join(players);
            sendMessage(player, new StringTextComponent("Players: " + playerString));
        } else {
            sendMessage(player, new StringTextComponent("No players defined in region."));
        }
    }

    public static void giveHelpMessage(PlayerEntity player) {
        sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
        sendMessage(player, "help.players.1");
        sendMessage(player, "help.players.2");
        sendMessage(player, "help.players.3");
        sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
    }

}
