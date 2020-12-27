package fr.mosca421.worldprotector.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class Region implements INBTSerializable<CompoundNBT> {

	private AxisAlignedBB area;
	private final Set<String> flags = new HashSet<>();
	private String name;
	private int priority = 2;
	private String dimension;
	private String enterMessage = "";
	private String exitMessage = "";
	private String enterMessageSmall = "";
	private String exitMessageSmall = "";
	private List<String> playerList = new ArrayList<>();

	public Region() {
	}

	public Region(String name, AxisAlignedBB area, String dimension) {
		this.name = name;
		this.area = area;
		this.dimension = dimension;
	}

	public AxisAlignedBB getArea() {
		return area;
	}

	public void setArea(AxisAlignedBB area) {
		this.area = area;
	}

	public Set<String> getFlags() {
		return flags;
	}

	public void addFlag(String flag) {
		this.flags.add(flag);
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

	public List<String> getPlayerList() {
		return playerList;
	}

	public boolean isInPlayerList(PlayerEntity name) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile()) != null)
			if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile()).getPermissionLevel() == 4)
				return true;

		if (playerList.contains(name.getUniqueID().toString()))
			return true;

		return false;
	}

	public boolean addPlayer(String name) {
		if (!playerList.contains(name)) {
			playerList.add(name);
			return true;
		}
		return false;
	}

	public boolean removePlayer(String name) {
		if (playerList.contains(name)) {
			playerList.remove(name);
			return true;
		}
		return false;
	}

	public void setPlayerList(List<String> playerList) {
		this.playerList = playerList;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("name", this.name);
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
		ListNBT flagList = new ListNBT();
		for (String flag : flags) {
			flagList.add(StringNBT.valueOf(flag));
		}
		nbt.put("flags", flagList);

		ListNBT playerLists = new ListNBT();
		for (String player : playerList) {
			playerLists.add(StringNBT.valueOf(player));
		}
		// Changed: playerlist should be put instead of flagList, right?
		nbt.put("playerList", playerLists);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.name = nbt.getString("name");
		this.area = new AxisAlignedBB(nbt.getInt("minX"), nbt.getInt("minY"), nbt.getInt("minZ"), nbt.getInt("maxX"), nbt.getInt("maxY"), nbt.getInt("maxZ"));
		this.priority = nbt.getInt("priority");
		this.dimension = nbt.getString("dimension");
		this.enterMessage = nbt.getString("enterMessage");
		this.exitMessage = nbt.getString("exitMessage");
		this.enterMessageSmall = nbt.getString("enterMessageSmall");
		this.exitMessageSmall = nbt.getString("exitMessageSmall");
		ListNBT flagsList = nbt.getList("flags", NBT.TAG_STRING);
		for (int i = 0; i < flagsList.size(); i++) {
			flags.add(flagsList.getString(i));
		}

		ListNBT playerLists = nbt.getList("playerList", NBT.TAG_STRING);
		for (int i = 0; i < playerLists.size(); i++) {
			playerList.add(playerLists.getString(i));
		}

	}

}
