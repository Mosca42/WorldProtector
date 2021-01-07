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
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

// TODO: Work on immutability
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

	public Region(CompoundNBT nbt) {
		this.players = new HashSet<>();
		this.flags = new HashSet<>();
		deserializeNBT(nbt);
	}

	public Region(String name, AxisAlignedBB area, String dimension) {
		this.name = name;
		this.area = area;
		this.dimension = dimension;
		this.players = new HashSet<>();
		this.flags = new HashSet<>();
	}

	public AxisAlignedBB getArea() {
		return area;
	}

	public BlockPos getCenterPos(){
		double middleX = (this.area.maxX + this.area.minX) / 2;
		double middleZ = (this.area.maxZ + this.area.minZ) / 2;
		return new BlockPos(middleX, middleZ, this.area.maxY);
	}

	public void setArea(AxisAlignedBB area) {
		this.area = area;
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

	public String getEnterMessage() {
		return enterMessage;
	}

	public void setEnterMessage(String enterMessage) {
		this.enterMessage = enterMessage;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	public String getEnterMessageSmall() {
		return enterMessageSmall;
	}

	public void setEnterMessageSmall(String enterMessageSmall) {
		this.enterMessageSmall = enterMessageSmall;
	}

	public String getExitMessageSmall() {
		return exitMessageSmall;
	}

	public void setExitMessageSmall(String exitMessageSmall) {
		this.exitMessageSmall = exitMessageSmall;
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

	public boolean addPlayer(String playerUUID) {
		return players.add(playerUUID);
	}

	public boolean removePlayer(String playerUUID) {
		return players.remove(playerUUID);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("name", name);
		nbt.putInt("minX", (int) area.minX);
		nbt.putInt("minY", (int) area.minY);
		nbt.putInt("minZ", (int) area.minZ);
		nbt.putInt("maxX", (int) area.maxX);
		nbt.putInt("maxY", (int) area.maxY);
		nbt.putInt("maxZ", (int) area.maxZ);
		nbt.putInt("priority", priority);
		nbt.putString("dimension", dimension);
		nbt.putString("enterMessage", enterMessage);
		nbt.putString("exitMessage", exitMessage);
		nbt.putString("enterMessageSmall", enterMessageSmall);
		nbt.putString("exitMessageSmall", exitMessageSmall);
		ListNBT flagsNBT = new ListNBT();
		flagsNBT.addAll(flags.stream()
				.map(StringNBT::valueOf)
				.collect(Collectors.toSet()));
		nbt.put("flags", flagsNBT);

		ListNBT playersNBT = new ListNBT();
		playersNBT.addAll(players.stream()
				.map(StringNBT::valueOf)
				.collect(Collectors.toSet()));
		nbt.put("players", playersNBT);
		return nbt;
	}

	private AxisAlignedBB areaFromNBT(CompoundNBT nbt){
		return new AxisAlignedBB(
				nbt.getInt("minX"), nbt.getInt("minY"), nbt.getInt("minZ"),
				nbt.getInt("maxX"), nbt.getInt("maxY"), nbt.getInt("maxZ")
		);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.name = nbt.getString("name");
		this.area = areaFromNBT(nbt);
		this.priority = nbt.getInt("priority");
		this.dimension = nbt.getString("dimension");
		this.enterMessage = nbt.getString("enterMessage");
		this.exitMessage = nbt.getString("exitMessage");
		this.enterMessageSmall = nbt.getString("enterMessageSmall");
		this.exitMessageSmall = nbt.getString("exitMessageSmall");
		this.flags.clear();
		ListNBT flagsList = nbt.getList("flags", NBT.TAG_STRING);
		for (int i = 0; i < flagsList.size(); i++) {
			flags.add(flagsList.getString(i));
		}
		this.players.clear();
		ListNBT playerLists = nbt.getList("players", NBT.TAG_STRING);
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
}
