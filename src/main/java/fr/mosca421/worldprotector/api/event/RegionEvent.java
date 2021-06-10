package fr.mosca421.worldprotector.api.event;

import fr.mosca421.worldprotector.core.IRegion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public abstract class RegionEvent extends Event {

    private final IRegion region;
    private final PlayerEntity player;

    public RegionEvent(IRegion region, PlayerEntity player) {
        this.region = region;
        this.player = player;
    }

    public IRegion getRegion() {
        return region;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public static class CreateRegionEvent extends RegionEvent {

        public CreateRegionEvent(IRegion region, PlayerEntity player) {
            super(region, player);
        }
    }

    public static class RemoveRegionEvent extends RegionEvent {

        public RemoveRegionEvent(IRegion region, PlayerEntity player) {
            super(region, player);
        }
    }

    public static class UpdateRegionEvent extends RegionEvent {

        public UpdateRegionEvent(IRegion region, PlayerEntity player) {
            super(region, player);
        }
    }
}


