package fr.mosca421.worldprotector.data;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class RegionManager extends WorldSavedData {

	private static final String DATA_NAME = WorldProtector.MODID;
	private static final String TAG_REGIONS = "regions";
	// Data instance
	private static RegionManager clientRegionCopy = new RegionManager();

	// Dimension -> ( RegionName -> Region )
	private static final Map<RegistryKey<World>, DimensionRegionCache> regionMap = new HashMap<>();

	private RegionManager() {
		super(DATA_NAME);
	}

	public Optional<DimensionRegionCache> getRegionsForDim(RegistryKey<World> dim){
		if (regionMap.containsKey(dim)) {
			return Optional.of(regionMap.get(dim));
		}
		return Optional.empty();
	}

	public Optional<Region> getRegionInDim(RegistryKey<World> dim, String regionName) {
		if (regionMap.containsKey(dim) && regionMap.get(dim).containsKey(regionName)) {
			return Optional.of(regionMap.get(dim).get(regionName));
		}
		return Optional.empty();
	}

	private Optional<RegistryKey<World>> getDimensionOfRegion(String regionName) {
		return regionMap.entrySet().stream()
				.filter( entry -> entry.getValue().containsKey(regionName))
				.map(Map.Entry::getKey)
				.findFirst();
	}

	public void addRegionToDim(Region region) {
		RegistryKey<World> dim = region.getDimension();
		if (regionMap.containsKey(dim)) {
			regionMap.get(dim).put(region.getName() ,region);
		} else {
			DimensionRegionCache initMapForDim = new DimensionRegionCache();
			initMapForDim.put(region.getName(), region);
			regionMap.put(dim, initMapForDim);
		}
		markDirty();
	}

	public boolean setActiveState(String regionName, boolean active){
		Optional<Region> maybeRegion = getRegion(regionName);
		if (maybeRegion.isPresent()) {
			Region region = maybeRegion.get();
			boolean wasUpdated = regionMap.get(region.getDimension()).setIsActive(regionName, active);
			if (wasUpdated) {
				markDirty();
			}
			return wasUpdated;
		} else {
			return false;
		}
	}

	public boolean isActive(String regionName){
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			DimensionRegionCache cache = maybeCache.get();
			return cache.isActive(regionName);
		}
		return false;
	}

	public Collection<Region> getAllRegions(){
		return regionMap.values().stream()
				.flatMap( regionCache -> regionCache.getRegions().stream())
				.collect(Collectors.toList());
	}

	public Collection<Region> getRegions(RegistryKey<World> dim){
		if (regionMap.containsKey(dim)) {
			return regionMap.get(dim).getRegions();
		}
		return Collections.emptySet();
	}

	public Collection<String> getAllRegionNames(){
		return regionMap.values().stream()
				.flatMap( regionCache -> regionCache.getRegionNames().stream())
				.collect(Collectors.toList());
	}

	public Collection<String> getRegionNames(RegistryKey<World> dim){
		if (regionMap.containsKey(dim)) {
			return regionMap.get(dim).getRegionNames();
		}
		return Collections.emptySet();
	}

	public boolean removeRegion(String regionName, RegistryKey<World> dim) {
		if (regionMap.containsKey(dim)) {
			Region removed = regionMap.get(dim).remove(regionName);
			markDirty();
			return removed != null;
		}
		return false;
	}

	public Region removeRegion(String regionName) {
		Optional<Region> maybeRegion = getRegion(regionName);
		if (maybeRegion.isPresent()) {
			Region region = maybeRegion.get();
			Region removed = regionMap.get(region.getDimension()).removeRegion(regionName);
			markDirty();
			return removed;
		} else {
			return null;
		}
	}

	private void removeRegion(Region region) {
		if (containsDimensionFor(region)) {
			regionMap.get(region.getDimension()).remove(region.getName());
			markDirty();
		}
	}


	public void clearRegions(){
		regionMap.forEach( (dim, cache) -> cache.clearRegions());
		markDirty();
	}

	public void updateRegion(Region newRegion){
		RegistryKey<World> dim = newRegion.getDimension();
		if (regionMap.containsKey(dim)) {
			regionMap.get(dim).updateRegion(newRegion);
			markDirty();
		}
	}

	public Optional<Region> getRegion(String regionName){
		return regionMap.values().stream()
				.filter(regionCache -> regionCache.containsKey(regionName)) // one remaining
				.map(regionCache -> regionCache.getRegion(regionName))
				.findFirst();
	}

	/**
	 * Prefer this getRegion
	 * @param regionName
	 * @param dim
	 * @return
	 */
	public Optional<Region> getRegion(String regionName, RegistryKey<World> dim){
		if (regionMap.containsKey(dim) && regionMap.get(dim).containsKey(regionName)) {
			return Optional.of(regionMap.get(dim).getRegion(regionName));
		}
		return Optional.empty();
	}

	public Optional<DimensionRegionCache> getRegionDimCache(String regionName){
		return regionMap.values().stream()
				.filter( regionCache -> regionCache.containsKey(regionName))
				.findFirst();
	}

	public boolean containsRegion(String regionName, RegistryKey<World> dim){
		if (regionMap.containsKey(dim)) {
			return regionMap.get(dim).containsKey(regionName);
		}
		return false;
	}

	public boolean containsRegion(String regionName){
		return regionMap.values().stream()
				.anyMatch(regionCache -> regionCache.containsKey(regionName));
	}

	public boolean containsDimensionFor(Region region) {
		return regionMap.containsKey(region.getDimension());
	}

	public void addRegion(Region region) {
		if (regionMap.containsKey(region.getDimension())) {
			regionMap.get(region.getDimension()).addRegion(region);
		} else {
			DimensionRegionCache newCache = new DimensionRegionCache();
			newCache.addRegion(region);
			regionMap.put(region.getDimension(), newCache);
		}
		markDirty();
	}

	// Flag methods
	public Set<String> getRegionFlags(String regionName, RegistryKey<World> dim){
		if (regionMap.containsKey(dim)) {
			return regionMap.get(dim).getFlags(regionName);
		}
		return new HashSet<>();
	}

	/**
	 * Always check contains first!
	 * @param region
	 * @return
	 */
	private DimensionRegionCache getCache(Region region) {
		return regionMap.get(region.getDimension());
	}

	public boolean removeFlag(Region region, String flag){
		if (containsDimensionFor(region)) {
			boolean wasRemoved = getCache(region).removeFlag(region, flag);
			if (wasRemoved) {
				markDirty();
			}
			return wasRemoved;
		}
		return false;
	}

	public boolean addFlag(Region region, String flag){
		if (containsDimensionFor(region)) {
			boolean wasAdded = getCache(region).addFlag(region, flag);
			if (wasAdded) {
				markDirty();
			}
			return wasAdded;
		}
		return false;
	}

	public List<String> removeFlags(String regionName, List<String> flags) {
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			List<String> removed = maybeCache.get().removeFlags(regionName, flags);
			markDirty();
			return removed;
		} else {
			return new ArrayList<>();
		}
	}

	public List<String> addFlags(String regionName, List<String> flags){
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			List<String> added = maybeCache.get().addFlags(regionName, flags);
			markDirty();
			return added;
		} else {
			return new ArrayList<>();
		}
	}

	/* Player related methods */

	public Set<String> getRegionPlayers(String regionName) {
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			DimensionRegionCache regionCache = maybeCache.get();
			if (regionCache.containsKey(regionName)) {
				return regionCache.getRegion(regionName).getPlayers();
			}
		}
		return new HashSet<>();
	}

	public Set<String> getRegionPlayers(String regionName, RegistryKey<World> dim) {
		if (regionMap.containsKey(dim) && regionMap.get(dim).containsKey(regionName)) {
			return regionMap.get(dim).getPlayers(regionName);
		}
		return new HashSet<>();
	}

	public boolean addPlayer(String regionName, PlayerEntity player){
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			boolean wasAdded = maybeCache.get().addPlayer(regionName, player);
			if (wasAdded) {
				markDirty();
			}
			return wasAdded;
		}
		return false;
	}

	public List<PlayerEntity> addPlayers(String regionName, List<PlayerEntity> playersToAdd) {
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			List<PlayerEntity> added = maybeCache.get().addPlayers(regionName, playersToAdd);
			markDirty();
			return added;
		} else {
			return new ArrayList<>();
		}
	}

	public boolean removePlayer(String regionName, PlayerEntity player){
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			boolean wasRemoved = maybeCache.get().removePlayer(regionName, player);
			if (wasRemoved) {
				markDirty();
			}
			return wasRemoved;
		}
		return false;
	}

	public List<PlayerEntity> removePlayers(String regionName, List<PlayerEntity> playersToRemove) {
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		if (maybeCache.isPresent()) {
			List<PlayerEntity> removed = maybeCache.get().removePlayers(regionName, playersToRemove);
			markDirty();
			return removed;
		} else {
			return new ArrayList<>();
		}
	}

	public boolean forbidsPlayer(String regionName, PlayerEntity player) {
		Optional<DimensionRegionCache> maybeCache = getRegionDimCache(regionName);
		return maybeCache
				.map(dimensionRegionCache -> dimensionRegionCache.forbidsPlayer(regionName, player))
				.orElse(true);
	}

	// Data
	/**
	 * Reads region compount nbt and puts it back into the regionMap
	 * @param nbt
	 */
	@Override
	public void read(CompoundNBT nbt) {
		clearRegions();
		ListNBT regionsList = nbt.getList(TAG_REGIONS, NBT.TAG_COMPOUND);
		for (int i = 0; i < regionsList.size(); i++) {
			Region region = new Region(regionsList.getCompound(i));
			if (regionMap.containsKey(region.getDimension())) {
				regionMap.get(region.getDimension()).put(region.getName(), region);
			} else {
				DimensionRegionCache newCache = new DimensionRegionCache();
				newCache.put(region.getName(), region);
				regionMap.put(region.getDimension(), newCache);
			}
		}
	}

	/**
	 * Writes the content (regions) of the regionMap.values() to a compound nbt object
	 * @param compound
	 * @return
	 */
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT regionsList = new ListNBT();
		for (DimensionRegionCache regionCache : regionMap.values()) {
			for (Region region : regionCache.getRegions()) {
				regionsList.add(region.serializeNBT());
			}
		}
		compound.put(TAG_REGIONS, regionsList);
		return compound;
	}

	public static void onServerStarting(FMLServerStartingEvent event) {
		try {
			ServerWorld world = Objects.requireNonNull(event.getServer().getWorld(World.OVERWORLD));
			if (!world.isRemote) {
				DimensionSavedDataManager storage = world.getSavedData();
				RegionManager data = storage.getOrCreate(RegionManager::new, DATA_NAME);
				WorldProtector.LOGGER.debug("Loaded dimension regions successfully");
				clientRegionCopy = data;
			}
		} catch (NullPointerException npe) {
			WorldProtector.LOGGER.error("Loading dimension regions failed");
		}
	}

	public static RegionManager get() {
		if (clientRegionCopy == null) {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if (server != null) {
				ServerWorld overworld = server.getWorld(World.OVERWORLD);
				if (overworld != null && !overworld.isRemote) {
					DimensionSavedDataManager storage = overworld.getSavedData();
					clientRegionCopy = storage.getOrCreate(RegionManager::new, DATA_NAME);
				}
			}
		}
		return clientRegionCopy;
	}
}
