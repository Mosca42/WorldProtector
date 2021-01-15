package fr.mosca421.worldprotector.core;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fr.mosca421.worldprotector.util.RegionFlagUtils;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Region implements INBTSerializable<CompoundNBT> {

	private AxisAlignedBB area;
	private String name;
	private int priority = 2;
	private String dimension;
	private String enterMessage = Strings.EMPTY;
	private String exitMessage = Strings.EMPTY;
	private String enterMessageSmall = Strings.EMPTY;
	private String exitMessageSmall = Strings.EMPTY;
	private final Set<String> flags;
	private final Set<String> players;
	private boolean isActive;

	// nbt keys
	public static final String NAME = "name";
	public static final String DIM = "dimension";
	public static final String MIN_X = "minX";
	public static final String MIN_Y = "minY";
	public static final String MIN_Z = "minZ";
	public static final String MAX_X = "maxX";
	public static final String MAX_Y = "maxY";
	public static final String MAX_Z = "maxZ";
	public static final String PRIORITY = "priority";
	public static final String ACTIVE = "active";
	public static final String PLAYERS = "players";
	public static final String FLAGS = "flags";
	public static final String ENTER_MSG_1 = "enter_msg";
	public static final String ENTER_MSG_2 = "enter_msg_small";
	public static final String EXIT_MSG_1 = "exit_msg";
	public static final String EXIT_MSG_2 = "exit_msg_small";

	public Region(CompoundNBT nbt) {
		this.players = new HashSet<>();
		this.flags = new HashSet<>();
		deserializeNBT(nbt);
	}

	public Region(String name, AxisAlignedBB area, String dimension) {
		this.name = name;
		this.area = area;
		this.dimension = dimension;
		this.isActive = true;
		this.players = new HashSet<>();
		this.flags = new HashSet<>();
	}

	public AxisAlignedBB getArea() {
		return area;
	}

	// TODO: Try to get lowest possible safe Y position
	public BlockPos getCenterPos(){
		double middleX = (this.area.maxX + this.area.minX) / 2;
		double middleZ = (this.area.maxZ + this.area.minZ) / 2;
		// area.getCenter()
		return new BlockPos(middleX, this.area.maxY, middleZ);
	}

	public Set<String> getFlags() {
		return flags;
	}

	public boolean addFlag(String flag) {
		return this.flags.add(flag);
	}

	public boolean removeFlag(String flag) {
		return this.flags.remove(flag);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public String getDimension() {
		return dimension;
	}

	public Set<String> getPlayers() {
		return players;
	}

	/**
	 * Checks if the player is defined in the regions player list OR whether the player is an operator.
	 * Usually this check is needed when an event occurs and it needs to be checked whether
	 * the player has a specific permission to perform an action in the region.
	 * @param player to be checked
	 * @return true if player is in region list or is an operator, false otherwise
	 */
	public boolean permits(PlayerEntity player) {
		if (RegionFlagUtils.isOp(player)) {
			return true;
		}
		return players.contains(player.getUniqueID().toString());
	}

	public boolean forbids(PlayerEntity player) {
		return !this.permits(player);
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean addPlayer(String playerUUID) {
		return players.add(playerUUID);
	}

	public boolean removePlayer(String playerUUID) {
		return players.remove(playerUUID);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(NAME, name);
		nbt.putInt(MIN_X, (int) area.minX);
		nbt.putInt(MIN_Y, (int) area.minY);
		nbt.putInt(MIN_Z, (int) area.minZ);
		nbt.putInt(MAX_X, (int) area.maxX);
		nbt.putInt(MAX_Y, (int) area.maxY);
		nbt.putInt(MAX_Z, (int) area.maxZ);
		nbt.putInt(PRIORITY, priority);
		nbt.putString(DIM, dimension);
		nbt.putBoolean(ACTIVE, isActive);
		nbt.putString(ENTER_MSG_1, enterMessage);
		nbt.putString(ENTER_MSG_2, enterMessageSmall);
		nbt.putString(EXIT_MSG_1, exitMessage);
		nbt.putString(EXIT_MSG_2, exitMessageSmall);
		ListNBT flagsNBT = new ListNBT();
		flagsNBT.addAll(flags.stream()
				.map(StringNBT::valueOf)
				.collect(Collectors.toSet()));
		nbt.put(FLAGS, flagsNBT);

		ListNBT playersNBT = new ListNBT();
		playersNBT.addAll(players.stream()
				.map(StringNBT::valueOf)
				.collect(Collectors.toSet()));
		nbt.put(PLAYERS, playersNBT);
		return nbt;
	}

	private AxisAlignedBB areaFromNBT(CompoundNBT nbt){
		return new AxisAlignedBB(
				nbt.getInt(MIN_X), nbt.getInt(MIN_Y), nbt.getInt(MIN_Z),
				nbt.getInt(MAX_X), nbt.getInt(MAX_Y), nbt.getInt(MAX_Z)
		);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.name = nbt.getString(NAME);
		this.area = areaFromNBT(nbt);
		this.priority = nbt.getInt(PRIORITY);
		this.dimension = nbt.getString(DIM);
		this.isActive = nbt.getBoolean(ACTIVE);
		this.enterMessage = nbt.getString(ENTER_MSG_2);
		this.enterMessageSmall = nbt.getString(ENTER_MSG_2);
		this.exitMessage = nbt.getString(EXIT_MSG_1);
		this.exitMessageSmall = nbt.getString(EXIT_MSG_2);
		this.flags.clear();
		ListNBT flagsList = nbt.getList(FLAGS, NBT.TAG_STRING);
		for (int i = 0; i < flagsList.size(); i++) {
			flags.add(flagsList.getString(i));
		}
		this.players.clear();
		ListNBT playerLists = nbt.getList(PLAYERS, NBT.TAG_STRING);
		for (int i = 0; i < playerLists.size(); i++) {
			players.add(playerLists.getString(i));
		}
	}

    public boolean containsFlag(String flag) {
		return flags.contains(flag);
    }

	public boolean containsFlag(RegionFlag flag) {
		return flags.contains(flag.toString());
	}

	public boolean containsPosition(BlockPos position){
		return this.area.contains(new Vector3d(position.getX(), position.getY(), position.getZ()));
	}
}
