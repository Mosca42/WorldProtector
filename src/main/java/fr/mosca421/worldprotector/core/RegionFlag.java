package fr.mosca421.worldprotector.core;

public enum RegionFlag {
    BREAK("break"),
    PLACE("place"),
    EXPLOSION("explosions"),
    SPAWNING_MONSTERS("mob-spawning-monsters"),
    SPAWNING_ANIMAL("mob-spawning-animal"),
    SPAWNING_ALL("mob-spawning-all"),
    USE("use"),
    CHEST_ACCESS("chest-access"),
    INVINCIBLE("invincible"),
    ENDERPEARL_TELEPORTATION("enderpearls"),
    ITEM_DROP("item-drop"),
    EXP_DROP("exp-drop"),
    EXPLOSION_CREEPER("creeper-explosions"),
    EXPLOSION_OTHER("other-explosions"),
    DAMAGE_PLAYERS("damage-players"),
    DAMAGE_ANIMALS("damage-animals"),
    DAMAGE_MONSTERS("damage-monsters"),
    SEND_MESSAGE("send-chat"),
    FALL_DAMAGE("fall-damage"),
    ITEM_PICKUP("pickup-item"),
    EXIT_MESSAGE_TITLE("exit-message"),
    ENTER_MESSAGE_TITLE("enter-message"),
    EXIT_MESSAGE_SUBTITLE("exit-message-small"),
    ENTER_MESSAGE_SUBTITLE("enter-message-small"),
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
}
