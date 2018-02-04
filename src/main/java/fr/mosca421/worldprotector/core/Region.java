package fr.mosca421.worldprotector.core;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Region implements INBTSerializable<NBTTagCompound> {

	private AxisRegions area;
	private Set<String> flags = new HashSet<String>();
	private String name;
	private int priority = 2;
	private int dimension;

	public Region() {
	}

	public Region(String name, AxisRegions area, int dimension) {
		this.name = name;
		this.area = area;
		this.dimension = dimension;
	}

	public AxisRegions getArea() {
		return area;
	}

	public void setArea(AxisRegions area) {
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

	public int getDimension() {
		return dimension;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("name", this.name);
		nbt.setInteger("minX", (int) area.minX);
		nbt.setInteger("minY", (int) area.minY);
		nbt.setInteger("minZ", (int) area.minZ);
		nbt.setInteger("maxX", (int) area.maxX);
		nbt.setInteger("maxY", (int) area.maxY);
		nbt.setInteger("maxZ", (int) area.maxZ);
		nbt.setInteger("priority", priority);
		nbt.setInteger("dimension", dimension);
		NBTTagList flagList = new NBTTagList();
		for (String flag : flags) {
			flagList.appendTag(new NBTTagString(flag));
		}
		nbt.setTag("flags", flagList);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.name = nbt.getString("name");
		this.area = new AxisRegions(nbt.getInteger("minX"), nbt.getInteger("minY"), nbt.getInteger("minZ"), nbt.getInteger("maxX"), nbt.getInteger("maxY"), nbt.getInteger("maxZ"));
		this.priority = nbt.getInteger("priority");
		this.dimension = nbt.getInteger("dimension");
		NBTTagList flagsList = nbt.getTagList("flags", NBT.TAG_STRING);
		for (int i = 0; i < flagsList.tagCount(); i++) {
			flags.add(flagsList.getStringTagAt(i));
		}
	}

}
