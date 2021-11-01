package fr.mosca421.worldprotector.core;

import fr.mosca421.worldprotector.util.PlayerUtils;
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

    boolean isMuted();

    void setIsActive(boolean isActive);

    void setIsMuted(boolean isMuted);

    boolean addPlayer(PlayerEntity player);

    boolean addPlayer(PlayerUtils.MCPlayerInfo playerInfo);

    boolean removePlayer(PlayerEntity player);

    boolean removePlayer(String playerName);

    boolean containsFlag(String flag);

    boolean containsFlag(RegionFlag flag);

    boolean containsPosition(BlockPos position);

    void setPriority(int priority);

    void setArea(AxisAlignedBB areaFromNBT);

    BlockPos getTpTarget();

    void setTpTarget(BlockPos pos);
}
