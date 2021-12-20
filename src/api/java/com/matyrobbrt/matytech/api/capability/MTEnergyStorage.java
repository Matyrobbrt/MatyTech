package com.matyrobbrt.matytech.api.capability;

import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class MTEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

	public MTEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
	}

	public MTEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		super(capacity, maxReceive, maxExtract, energy);
	}

	public static MTEnergyStorage fromNbt(CompoundNBT nbt) {
		return new MTEnergyStorage(nbt.getInt("capacity"), nbt.getInt("maxReceive"), nbt.getInt("maxExtract"),
				nbt.getInt("storedEnergy"));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		energy = nbt.getInt("storedEnergy");
		maxExtract = nbt.contains("maxExtract") ? nbt.getInt("maxExtract") : maxExtract;
		maxReceive = nbt.contains("maxReceive") ? nbt.getInt("maxReceive") : maxReceive;
		capacity = nbt.contains("capacity") ? nbt.getInt("capacity") : capacity;
		setChanged();
	}

	public CompoundNBT serialize(CompoundNBT nbt) {
		nbt.putInt("storedEnergy", energy);
		nbt.putInt("maxExtract", maxExtract);
		nbt.putInt("maxReceive", maxReceive);
		nbt.putInt("capacity", capacity);
		return nbt;
	}

	@Override
	public CompoundNBT serializeNBT() {
		return serialize(new CompoundNBT());
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int ret = super.extractEnergy(maxExtract, simulate);
		setChanged();
		return ret;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int ret = super.receiveEnergy(maxReceive, simulate);
		setChanged();
		return ret;
	}

	public void setChanged() {
	}
}
