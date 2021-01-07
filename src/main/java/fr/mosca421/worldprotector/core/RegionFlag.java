package fr.mosca421.worldprotector.core;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum RegionFlag {
    BREAK("break"),
    PLACE("place"),
    ENTITY_PLACE("entity-place"),
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
    SPAWNING_MONSTERS("mob-spawning-monsters"),
    SPAWNING_ANIMAL("mob-spawning-animal"),
    SPAWNING_ALL("mob-spawning-all"),
    //
    USE("use"),
    CHEST_ACCESS("chest-access"),
    USE_ENDERPEARL_FROM("enderpearl-from"),
    USE_ENDERPEARL_TO("enderpearl-to"),
    // Possible other flags related to this:
    // - enderman-teleport: prevents enderman from teleporting
    // - shulker-teleport: prevents shulkers from teleporting
    //
    ITEM_DROP("item-drop"),
    ITEM_PICKUP("item-pickup"),
    EXP_DROP("exp-drop"),
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
    EXIT_MESSAGE_TITLE("exit-message"),
    ENTER_MESSAGE_TITLE("enter-message"),
    EXIT_MESSAGE_SUBTITLE("exit-message-small"),
    ENTER_MESSAGE_SUBTITLE("enter-message-small"),
    //
    BLOCK_ENTER("block-enter"),
    BLOCK_EXIT("block-exit");

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
    public static Set<String> getFlags() {
        return Arrays.stream(RegionFlag.values())
                .map(RegionFlag::toString)
                .collect(Collectors.toSet());
    }

    public static Optional<RegionFlag> fromString(String flagIdentifier){
        return Arrays.stream(values())
                .filter(flag -> flag.flagIdentifier.equals(flagIdentifier))
                .findFirst();
    }
}
