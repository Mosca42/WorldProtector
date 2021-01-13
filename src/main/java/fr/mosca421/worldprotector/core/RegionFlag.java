package fr.mosca421.worldprotector.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    SPAWING_EXP("spawning-exp"),
    //
    USE("use"), // Buttons, Doors, Lever, etc  // TODO: Currently not working correctly
    USE_BONEMEAL("use-bonemeal"),
    CHEST_ACCESS("access-container"),
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
    EXP_DROP_ALL("exp-drop-all"), // also includes blocks (furnace for example)
    EXP_DROP_MONSTER("exp-drop-monsters"), // only hostile mobs
    EXP_DROP_OTHER("exp-drop-other"), // non-hostile: animals, villagers,...
    EXP_PICKUP("exp-pickup"), // TODO: Currently not working correctly
    LEVEL_FREEZE("level-freeze"),
    EXP_CHANGE("exp-freeze"), // TODO: Currently not working correctly
    //
    DAMAGE_PLAYERS("damage-players"),
    DAMAGE_ANIMALS("damage-animals"),
    DAMAGE_MONSTERS("damage-monsters"),
    DAMAGE_VILLAGERS("damage-villagers"),
    //
    INVINCIBLE("invincible"),
    FALL_DAMAGE("fall-damage"),
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
