package fr.mosca421.worldprotector.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RegionProxy implements IRegion{

    private final String regionName;
    private Region backingRegion;

    public RegionProxy(String regionName){
        this.regionName = regionName;
    }

    @Override
    public AxisAlignedBB getArea() {
        return null;
    }

    @Override
    public Set<String> getFlags() {
        return null;
    }

    @Override
    public boolean addFlag(String flag) {
        return false;
    }

    @Override
    public boolean removeFlag(String flag) {
        return false;
    }

    @Override
    public String getName() {
        return regionName;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public RegistryKey<World> getDimension() {
        return null;
    }

    @Override
    public BlockPos getTpPos(World world) {
        return null;
    }

    @Override
    public Map<UUID, String> getPlayers() {
        return null;
    }

    @Override
    public Set<String> getPlayerNames() {
        return null;
    }

    @Override
    public boolean permits(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean forbids(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setIsActive(boolean isActive) {

    }

    @Override
    public boolean addPlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean removePlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean containsFlag(String flag) {
        return false;
    }

    @Override
    public boolean containsFlag(RegionFlag flag) {
        return false;
    }

    @Override
    public boolean containsPosition(BlockPos position) {
        return false;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public void setArea(AxisAlignedBB areaFromNBT) {

    }

    @Override
    public BlockPos getTpTarget() {
        return null;
    }

    @Override
    public void setTpTarget(BlockPos pos) {

    }

    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
