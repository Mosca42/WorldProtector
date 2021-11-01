package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.command.Command;
import fr.mosca421.worldprotector.config.ServerConfigBuilder;
import fr.mosca421.worldprotector.data.RegionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftGame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.config.ServerConfigBuilder.WP_CMD;
import static fr.mosca421.worldprotector.util.MessageUtils.*;
import static fr.mosca421.worldprotector.util.PlayerUtils.*;

public final class RegionPlayerUtils {

    private RegionPlayerUtils() {
    }

    public static void addPlayers(String regionName, PlayerEntity sourcePlayer, List<PlayerEntity> playersToAdd) {
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
                // TODO: rework lang key
                sendMessage(sourcePlayer, new TranslationTextComponent( "message.players.add.none", playerString, regionName));
            }
        } else {
            sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
        }
    }

    public static void addPlayer(String regionName, PlayerEntity sourcePlayer, MCPlayerInfo playerInfo) {
        if (RegionManager.get().containsRegion(regionName)) {
            String playerToAddName = playerInfo.playerName;
            if (RegionManager.get().addPlayer(regionName, playerInfo)) {
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.addplayer", playerToAddName, regionName));
            } else {
                // Player already defined in this region -> Message needed or silent acknowledgement?
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.errorplayer", regionName, playerToAddName));
            }
        } else {
            sendMessage(sourcePlayer,  new TranslationTextComponent("message.region.unknown", regionName));
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

    public static void removePlayer(String regionName, PlayerEntity sourcePlayer, String playerToRemoveName) {
        if (RegionManager.get().containsRegion(regionName)) {
            if (RegionManager.get().removePlayer(regionName, playerToRemoveName)) {
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.removeplayer", playerToRemoveName, regionName));
            } else {
                // Player was not present in this region -> Message needed or silent acknowledgement?
                sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknownplayer", regionName, playerToRemoveName));
            }
        } else {
            sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
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

    public static boolean hasNeededOpLevel(PlayerEntity player) {
        OpEntry opPlayerEntry = ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .getOppedPlayers()
                .getEntry(player.getGameProfile());
        if (opPlayerEntry != null) {
            return opPlayerEntry.getPermissionLevel() >= ServerConfigBuilder.OP_COMMAND_PERMISSION_LEVEL.get();
        }
        return false;
    }

    public static void listPlayersInRegion(String regionName, PlayerEntity player) {
        RegionManager.get().getRegion(regionName).ifPresent(region -> {
            // TODO: lang-key   "chat.header.region":"Players in Region '%s'"
            sendMessage(player, new TranslationTextComponent(TextFormatting.BOLD + "== Players in Region '" + regionName + "' =="));
            if (region.getPlayers().isEmpty()) {
                sendMessage(player, new TranslationTextComponent("message.region.info.noplayers"));
                return;
            }
            region.getPlayers().values().forEach(playerName -> {
                sendMessage(player, buildRemovePlayerLink(playerName, regionName));
            });
            sendMessage(player, new StringTextComponent(""));
        });
    }

    public static IFormattableTextComponent buildRemovePlayerLink(String playerName, String region) {
        String command =  "/" + WP_CMD + " " + Command.PLAYER + " " + Command.REMOVE + " " + region + " " + playerName;
        return new StringTextComponent(" - ")
                // TODO: Langkey and overload method with translatableComponent
                .appendSibling(buildRunCommandLink("x", command, TextFormatting.RED, "Remove player '" + playerName + "' from region " + "'" + region + "'"))
                .appendSibling(new StringTextComponent(" '" + playerName + "'"));
    }
}
