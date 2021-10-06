package fr.mosca421.worldprotector.core;

import fr.mosca421.worldprotector.util.RegionPlayerUtils;
import joptsimple.internal.Strings;
import net.minecraft.client.MinecraftGame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.*;
import java.util.stream.Collectors;

public class Region implements IRegion {

	private String name;
	private RegistryKey<World> dimension;
	private AxisAlignedBB area;
	private final Set<String> flags;
	private final Map<UUID, String> players;
	private boolean isActive;
	private int priority = 2;
	private boolean isMuted;
	private int tpTargetX;
	private int tpTargetY;
	private int tpTargetZ;
	private String enterMessage = Strings.EMPTY;
	private String exitMessage = Strings.EMPTY;
	private String enterMessageSmall = Strings.EMPTY;
	private String exitMessageSmall = Strings.EMPTY;
	// nbt keys
	public static final String TP_X = "tp_x";
	public static final String TP_Y = "tp_y";
	public static final String TP_Z = "tp_z";
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
	public static final String MUTED = "muted";

	public static final String VERSION = "version";
	public static final String DATA_VERSION = "2.1.4.0";

	public Region(CompoundNBT nbt) {
		this.flags = new HashSet<>();
		this.players = new HashMap<>();
		deserializeNBT(nbt);
	}

	public Region(String name, AxisAlignedBB area, RegistryKey<World> dimension) {
		this.name = name;
		this.area = area;
		this.tpTargetX = (int) this.area.getCenter().getX();
		this.tpTargetY = (int) this.area.getCenter().getY();
		this.tpTargetZ = (int) this.area.getCenter().getZ();
		this.dimension = dimension;
		this.isActive = true;
		this.isMuted = false;
		this.players = new HashMap<>();
		this.flags = new HashSet<>();
	}

	public Region(String name, AxisAlignedBB area, BlockPos tpPos, RegistryKey<World> dimension) {
		this.tpTargetX = tpPos.getX();
		this.tpTargetY = tpPos.getY();
		this.tpTargetZ = tpPos.getZ();
		this.name = name;
		this.area = area;
		this.dimension = dimension;
		this.isActive = true;
		this.isMuted = false;
		this.players = new HashMap<>();
		this.flags = new HashSet<>();
	}

	public Region(Region copy) {
		this.tpTargetX = copy.getTpTarget().getX();
		this.tpTargetY = copy.getTpTarget().getY();
		this.tpTargetZ = copy.getTpTarget().getZ();
		this.name = copy.getName();
		this.area = copy.getArea();
		this.dimension = copy.getDimension();
		this.isActive = copy.isActive();
		this.isMuted = copy.isMuted();
		this.players = copy.getPlayers();
		this.flags = copy.getFlags();
		this.priority = copy.getPriority();
		this.enterMessage = copy.getEnterMessage();
		this.exitMessage = copy.getExitMessage();
		this.enterMessageSmall = copy.getEnterMessageSmall();
		this.exitMessageSmall = copy.getExitMessageSmall();
	}

	public Region(IRegion copy) {
		this.tpTargetX = copy.getTpTarget().getX();
		this.tpTargetY = copy.getTpTarget().getY();
		this.tpTargetZ = copy.getTpTarget().getZ();
		this.name = copy.getName();
		this.area = copy.getArea();
		this.dimension = copy.getDimension();
		this.isActive = copy.isActive();
		this.players = copy.getPlayers();
		this.flags = copy.getFlags();
		this.priority = copy.getPriority();
	}

	@Override
	public AxisAlignedBB getArea() {
		return area;
	}

	public void setArea(AxisAlignedBB area) {
		this.area = area;
	}

	/**
	 * TODO: Does not check for harmful blocks though
	 *
	 * @param world
	 * @return
	 */
	public BlockPos getCenterSaveTpPos(World world){
		Vector3d center = area.getCenter();
		int highestNonBlockingY =  world.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) area.minX, (int) area.minZ);
		return new BlockPos(center.x, highestNonBlockingY + 1, center.z);
	}

	/**
	 * @param world
	 * @return
	 */
	@Override
	public BlockPos getTpPos(World world) {
		int highestNonBlockingY = world.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) area.minX, (int) area.minZ);
		return new BlockPos(area.minX, highestNonBlockingY + 1, area.minZ);
	}

	@Override
	public BlockPos getTpTarget() {
		return new BlockPos(this.tpTargetX, this.tpTargetY, this.tpTargetZ);
	}

	@Override
	public void setTpTarget(BlockPos tpPos) {
		this.tpTargetX = tpPos.getX();
		this.tpTargetY = tpPos.getY();
		this.tpTargetZ = tpPos.getZ();
	}

	@Override
	public Set<String> getFlags() {
		return flags;
	}

	/**
	 * True if added
	 *
	 * @param flag
	 * @return
	 */
	@Override
	public boolean addFlag(String flag) {
		return this.flags.add(flag);
	}

	/**
	 * true if removed
	 *
	 * @param flag
	 * @return
	 */
	@Override
	public boolean removeFlag(String flag) {
		return this.flags.remove(flag);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public String getDimensionString() {
		return dimension.getLocation().toString();
	}

	public String getEnterMessage() {
		return enterMessage;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public String getEnterMessageSmall() {
		return enterMessageSmall;
	}

	public String getExitMessageSmall() {
		return exitMessageSmall;
	}

	@Override
	public RegistryKey<World> getDimension() {
		return dimension;
	}

	@Override
	public Map<UUID, String> getPlayers() {
		return Collections.unmodifiableMap(this.players);
	}

	public Set<UUID> getPlayerUUIDs() {
		return Collections.unmodifiableSet(new HashSet<>(this.players.keySet()));
	}

	@Override
	public Set<String> getPlayerNames() {
		return Collections.unmodifiableSet(new HashSet<>(this.players.values()));
	}

	/**
	 * Checks if the player is defined in the regions player list OR whether the player is an operator.
	 * Usually this check is needed when an event occurs and it needs to be checked whether
	 * the player has a specific permission to perform an action in the region.
	 *
	 * @param player to be checked
	 * @return true if player is in region list or is an operator, false otherwise
	 */
	@Override
	public boolean permits(PlayerEntity player) {
		if (RegionPlayerUtils.hasNeededOpLevel(player)) {
			return true;
		}
		return players.containsKey(player.getUniqueID());
	}

	@Override
	public boolean forbids(PlayerEntity player) {
		return !this.permits(player);
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean isMuted() {
		return this.isMuted;
	}

	@Override
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public void setIsMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	public void activate() {
		this.isActive = true;
	}

	public void deactivate() {
		this.isActive = false;
	}

	@Override
	public boolean addPlayer(PlayerEntity player) {
		String oldPlayer = this.players.put(player.getUniqueID(), player.getName().getString());
		return !player.getName().getString().equals(oldPlayer);
	}

	@Override
	public boolean removePlayer(PlayerEntity player) {
		if (this.players.containsKey(player.getUniqueID())) {
			String oldPlayer = this.players.remove(player.getUniqueID());
			return oldPlayer != null;
		}
		return false;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(VERSION, DATA_VERSION);
		nbt.putString(NAME, name);
		nbt.putInt(MIN_X, (int) area.minX);
		nbt.putInt(MIN_Y, (int) area.minY);
		nbt.putInt(MIN_Z, (int) area.minZ);
		nbt.putInt(MAX_X, (int) area.maxX);
		nbt.putInt(MAX_Y, (int) area.maxY);
		nbt.putInt(MAX_Z, (int) area.maxZ);
		nbt.putInt(TP_X, this.tpTargetX);
		nbt.putInt(TP_Y, this.tpTargetY);
		nbt.putInt(TP_Z, this.tpTargetZ);
		nbt.putInt(PRIORITY, priority);
		nbt.putString(DIM, dimension.getLocation().toString());
		nbt.putBoolean(ACTIVE, isActive);
		nbt.putBoolean(MUTED, isMuted);
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

	private CompoundNBT migrateRegionData(CompoundNBT nbt){
		// TODO:

		nbt.putString(VERSION, DATA_VERSION);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		nbt = migrateRegionData(nbt);
		this.name = nbt.getString(NAME);
		this.area = areaFromNBT(nbt);
		this.tpTargetX = nbt.getInt(TP_X);
		this.tpTargetY = nbt.getInt(TP_Y);
		this.tpTargetZ = nbt.getInt(TP_Z);
		this.priority = nbt.getInt(PRIORITY);
		this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString(DIM)));
		this.isActive = nbt.getBoolean(ACTIVE);
		this.isMuted = nbt.getBoolean(MUTED);
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

	@Override
	public boolean containsFlag(String flag) {
		return flags.contains(flag);
	}

	public boolean containsFlag(RegionFlag flag) {
		return flags.contains(flag.toString());
	}

	@Override
	public boolean containsPosition(BlockPos position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		// INFO: this.area.contains(x,y,z); does not work. See implementation. Forge-Version 36.1.25
		return x >= this.area.minX && x <= this.area.maxX
				&& y >= this.area.minY && y <= this.area.maxY
				&& z >= this.area.minZ && z <= this.area.maxZ;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Region.class.getSimpleName() + "[", "]")
				.add("name='" + name + "'")
				.add("dimension=" + dimension.getLocation().toString())
				.add("area=" + area.toString())
				.add("flags=" + flags)
				.add("players=" + players)
				.add("isActive=" + isActive)
				.add("isMuted=" + isMuted)
				.add("priority=" + priority)
				.add("enterMessage='" + enterMessage + "'")
				.add("exitMessage='" + exitMessage + "'")
				.add("enterMessageSmall='" + enterMessageSmall + "'")
				.add("exitMessageSmall='" + exitMessageSmall + "'")
				.add("tpTarget=[" + tpTargetX + ", " + tpTargetY + ", " + tpTargetZ + "]")
				.toString();
	}
}
