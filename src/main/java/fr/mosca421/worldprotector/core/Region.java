package fr.mosca421.worldprotector.core;

import java.util.*;
import java.util.stream.Collectors;

import fr.mosca421.worldprotector.util.RegionPlayerUtils;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Region implements INBTSerializable<CompoundNBT> {

	private String name;
	private RegistryKey<World> dimension;
	private AxisAlignedBB area;
	private final Set<String> flags;
	private final Map<UUID, String> players;
	private boolean isActive;
	private int priority = 2;
	private String enterMessage = Strings.EMPTY;
	private String exitMessage = Strings.EMPTY;
	private String enterMessageSmall = Strings.EMPTY;
	private String exitMessageSmall = Strings.EMPTY;
	// nbt keys
	public static final String NAME = "name";
	public static final String UUID = "uuid";
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
		this.flags = new HashSet<>();
		this.players = new HashMap<>();
		deserializeNBT(nbt);
	}

	public Region(String name, AxisAlignedBB area, RegistryKey<World> dimension) {
		this.name = name;
		this.area = area;
		this.dimension = dimension;
		this.isActive = true;
		this.players = new HashMap<>();
		this.flags = new HashSet<>();
	}

	public Region(Region copy){
		this.name = copy.name;
		this.area = copy.area;
		this.dimension = copy.dimension;
		this.isActive = copy.isActive;
		this.players = copy.players;
		this.flags = copy.flags;
		this.priority = copy.priority;
		this.enterMessage = copy.enterMessage;
		this.exitMessage = copy.exitMessage;
		this.enterMessageSmall = copy.enterMessageSmall;
		this.exitMessageSmall = copy.exitMessageSmall;
	}

	public AxisAlignedBB getArea() {
		return area;
	}

	public void setArea(AxisAlignedBB area) {
		this.area = area;
	}

	/**
	 * Does not check for harmful blocks though
	 * @param world
	 * @return
	 */
	public BlockPos getCenterSaveTpPos(World world){
		Vector3d center = area.getCenter();
		int highestNonBlockingY =  world.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) area.minX, (int) area.minZ);
		return new BlockPos(center.x, highestNonBlockingY + 1, center.z);
	}

	/**
	 *
	 * @param world
	 * @return
	 */
	public BlockPos getMinBorderTpPos(World world){
		int highestNonBlockingY =  world.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) area.minX, (int) area.minZ);
		return new BlockPos(area.minX, highestNonBlockingY + 1, area.minZ);
	}

	public Set<String> getFlags() {
		return flags;
	}

	/**
	 * True if added
	 * @param flag
	 * @return
	 */
	public boolean addFlag(String flag) {
		return this.flags.add(flag);
	}

	/**
	 * true if removed
	 * @param flag
	 * @return
	 */
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

	public String getDimensionString() {
		return dimension.getLocation().toString();
	}

	public RegistryKey<World> getDimension() {
		return dimension;
	}

	public Set<String> getPlayers() {
		return (Set<String>) this.players.values();
	}

	public Set<UUID> getPlayerUUIDs() {
		return this.players.keySet();
	}

	/**
	 * Checks if the player is defined in the regions player list OR whether the player is an operator.
	 * Usually this check is needed when an event occurs and it needs to be checked whether
	 * the player has a specific permission to perform an action in the region.
	 * @param player to be checked
	 * @return true if player is in region list or is an operator, false otherwise
	 */
	public boolean permits(PlayerEntity player) {
		if (RegionPlayerUtils.isOp(player)) {
			return true;
		}
		return players.containsKey(player.getUniqueID());
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

	public void activate(){
		this.isActive = true;
	}

	public void deactivate(){
		this.isActive = false;
	}

	public boolean addPlayer(PlayerEntity player) {
		String added = this.players.put(player.getUniqueID(), player.getName().toString());
		return added != null;
	}

	public boolean removePlayer(PlayerEntity player) {
		String removed = this.players.remove(player.getUniqueID());
		return removed != null;
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
		nbt.putString(DIM, dimension.getLocation().toString());
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

		// serialize player data
		ListNBT playerList = nbt.getList(PLAYERS, NBT.TAG_COMPOUND);
		players.forEach( (uuid, name) -> {
			CompoundNBT playerNBT = new CompoundNBT();
			playerNBT.putUniqueId(UUID, uuid);
			playerNBT.putString(NAME, name);
			playerList.add(playerNBT);
		});
		nbt.put(PLAYERS, playerList);
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
		this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString(DIM)));
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
		// deserialize player data
		this.players.clear();
		ListNBT playerLists = nbt.getList(PLAYERS, NBT.TAG_COMPOUND);
		for (int i = 0; i < playerLists.size(); i++) {
			CompoundNBT playerMapping = playerLists.getCompound(i);
			players.put(playerMapping.getUniqueId(UUID), playerMapping.getString(NAME));
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

	// TODO:
	public static Region read(PacketBuffer buf) {
		/*
		UUID waystoneUid = buf.readUniqueId();
		String name = buf.readString();
		boolean isGlobal = buf.readBoolean();
		RegistryKey<World> dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString(250)));
		BlockPos pos = buf.readBlockPos();

		Waystone waystone = new Waystone(waystoneUid, dimension, pos, false, null);
		waystone.setName(name);
		waystone.setGlobal(isGlobal);
		return waystone;
		*/
		return null;
	}

	public static void write(PacketBuffer buf, Region waystone) {
		/*
		buf.writeUniqueId(waystone.getWaystoneUid());
		buf.writeString(waystone.getName());
		buf.writeBoolean(waystone.isGlobal());
		buf.writeResourceLocation(waystone.getDimension().getLocation());
		buf.writeBlockPos(waystone.getPos());
		*/

	}
}
