package fr.mosca421.worldprotector.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum RegionFlag {
    ALL("all"),
    //
    BREAK("break"),
    PLACE("place"),
    ENTITY_PLACE("entity-place"), // TODO: needs testing
    //
    EXPLOSION_ENTITY("explosions-entities"),
    EXPLOSION_BLOCK("explosions-blocks"),
    EXPLOSION_CREEPER_BLOCK("creeper-explosion-entities"),
    EXPLOSION_CREEPER_ENTITY("creeper-explosion-blocks"),
    EXPLOSION_OTHER_BLOCKS("other-explosion-entities"),
    EXPLOSION_OTHER_ENTITY("other-explosion-blocks"),
    IGNITE_EXPLOSIVES("ignite-explosives"),
    //
    TOOL_SECONDARY_USE("tools-secondary"),
    AXE_STRIP("strip-wood"),
    HOE_TILL("till-farmland"),
    SHOVEL_PATH("shovel-path"),
    //
    TRAMPLE_FARMLAND("trample-farmland"),
    TRAMPLE_FARMLAND_PLAYER("trample-farmland-player"),
    TRAMPLE_FARMLAND_OTHER("trample-farmland-other"),
    //
    DRAGON_BLOCK_PROT("dragon-destruction"), // TODO: Currently not working correctly
    WITHER_BLOCK_PROT("wither-destruction"), // TODO: Currently not working correctly
    ZOMBIE_DOOR_PROT("zombie-destruction"), // TODO: Currently not working correctly
    LIGHTNING_PROT("lightning"),
    //
    ANIMAL_TAMING("animal-taming"),
    ANIMAL_BREEDING("animal-breeding"),
    ANIMAL_MOUNTING("animal-mounting"),
    ANIMAL_UNMOUNTING("animal-unmounting"), // TODO: Currently not working correctly
    //
    SPAWNING_MONSTERS("spawning-monsters"),
    SPAWNING_GOLEM("spawning-irongolem"),
    SPAWNING_ANIMAL("spawning-animal"),
    SPAWNING_ALL("spawning-all"),
    SPAWNING_XP("spawning-xp"),
    //
    USE("use"), // Buttons, Doors, Lever, etc
    USE_BONEMEAL("use-bonemeal"),
    CONTAINER_ACCESS("access-container"),
    ENDER_CHEST_ACCESS("access-enderchest"),
    USE_ENDERPEARL_FROM_REGION("enderpearl-from"),
    USE_ENDERPEARL_TO_REGION("enderpearl-to"),
    //
    ENDERMAN_TELEPORT_TO_REGION("enderman-teleport-to"),
    ENDERMAN_TELEPORT_FROM_REGION("enderman-teleport-from"),
    SHULKER_TELEPORT_TO_REGION("shulker-teleport-to"),
    SHULKER_TELEPORT_FROM_REGION("shulker-teleport-from"),
    //
    ITEM_DROP("item-drop"),
    ITEM_PICKUP("item-pickup"),
    LOOT_DROP("loot-drop"),
    XP_DROP_ALL("xp-drop-all"), // also includes blocks (furnace for example)
    XP_DROP_MONSTER("xp-drop-monsters"), // only hostile mobs
    XP_DROP_OTHER("xp-drop-other"), // non-hostile: animals, villagers,...
    XP_PICKUP("xp-pickup"),
    LEVEL_FREEZE("level-freeze"),
    XP_FREEZE("xp-freeze"),
    //
    ATTACK_PLAYERS("attack-players"),
    ATTACK_ANIMALS("attack-animals"),
    ATTACK_MONSTERS("attack-monsters"),
    ATTACK_VILLAGERS("attack-villagers"),
    //
    INVINCIBLE("invincible"),
    //
    FALL_DAMAGE("fall-damage"),
    FALL_DAMAGE_VILLAGERS("fall-damage-villagers"),
    FALL_DAMAGE_MONSTERS("fall-damage-monsters"),
    FALL_DAMAGE_ANIMALS("fall-damage-animals"),
    FALL_DAMAGE_PLAYERS("fall-damage-players"),
    //
    SEND_MESSAGE("send-chat"),
    EXIT_MESSAGE_TITLE("exit-message"), // TODO: Currently not working correctly
    ENTER_MESSAGE_TITLE("enter-message"), // TODO: Currently not working correctly
    EXIT_MESSAGE_SUBTITLE("exit-message-small"), // TODO: Currently not working correctly
    ENTER_MESSAGE_SUBTITLE("enter-message-small"), // TODO: Currently not working correctly
    //
    BLOCK_ENTER("block-enter"), // TODO: not yet implemented
    BLOCK_EXIT("block-exit"); // TODO: not yet implemented

    private final String flagIdentifier;

    RegionFlag(final String flagIdentifier) {
        this.flagIdentifier = flagIdentifier;
    }

    @Override
    public String toString() {
        return flagIdentifier;
    }

    /**
     * Checks if a flagIdentifier is defined within the RegionFlag enum.
     * Replaces the check of FlagsList.VALID_FLAGS.contains(flag).
     * @param flagIdentifier to be checked
     * @return true if flagIdentifier is defined within this enum, false otherwise
     */
    public static boolean contains(String flagIdentifier) {
        return Arrays.stream(RegionFlag.values())
                .anyMatch(flag -> flag.toString().equals(flagIdentifier));
    }

    /**
     * Returns a set of all flags with their string representation defined within this enum.
     * @return a set of all flagIdentifiers defined within RegionFlag
     */
    public static List<String> getFlags() {
        return Arrays.stream(RegionFlag.values())
                .map(RegionFlag::toString)
                .collect(Collectors.toList());
    }

    public static Optional<RegionFlag> fromString(String flagIdentifier){
        return Arrays.stream(values())
                .filter(flag -> flag.flagIdentifier.equals(flagIdentifier))
                .findFirst();
    }
}
