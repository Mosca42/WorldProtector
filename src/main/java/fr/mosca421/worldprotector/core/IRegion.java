package fr.mosca421.worldprotector.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IRegion extends INBTSerializable<CompoundNBT> {
    AxisAlignedBB getArea();

    Set<String> getFlags();

    boolean addFlag(String flag);

    boolean removeFlag(String flag);

    String getName();

    int getPriority();

    RegistryKey<World> getDimension();

    BlockPos getTpPos(World world);

    Map<UUID, String> getPlayers();

    Set<String> getPlayerNames();

    boolean permits(PlayerEntity player);

    boolean forbids(PlayerEntity player);

    boolean isActive();

    void setIsActive(boolean isActive);

    boolean addPlayer(PlayerEntity player);

    boolean removePlayer(PlayerEntity player);

    boolean containsFlag(String flag);

    boolean containsFlag(RegionFlag flag);

    boolean containsPosition(BlockPos position);

    void setPriority(int priority);

    void setArea(AxisAlignedBB areaFromNBT);

}
