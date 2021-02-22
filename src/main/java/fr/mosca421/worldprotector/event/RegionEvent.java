package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.core.Region;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public abstract class RegionEvent extends Event {

    private final Region region;
    private final PlayerEntity player;

    public RegionEvent(Region region, PlayerEntity player) {
        this.region = region;
        this.player = player;
    }

    public Region getRegion() {
        return region;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public static class CreateRegionEvent extends RegionEvent {

        public CreateRegionEvent(Region region, PlayerEntity player) {
            super(region, player);
        }
    }

    public static class RemoveRegionEvent extends RegionEvent {

        public RemoveRegionEvent(Region region, PlayerEntity player) {
            super(region, player);
        }
    }

    public static class UpdateRegionEvent extends RegionEvent {

        public UpdateRegionEvent(Region region, PlayerEntity player) {
            super(region, player);
        }
    }
}


