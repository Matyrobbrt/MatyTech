package com.matyrobbrt.matytech.api.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageItemStack extends EnergyStorage implements ICapabilityProvider {

	public static final String NBT_KEY = "Energy";

	private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

	@Nonnull
	protected ItemStack container;

	public EnergyStorageItemStack(@Nonnull ItemStack container, int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
		this.container = container;
		this.capacity = capacity;
	}

	@Nonnull
	public ItemStack getContainer() { return container; }

	@Override
	public int getEnergyStored() {

		if (getContainer().hasTag()) { return getContainer().getOrCreateTagElement(NBT_KEY).getInt("EnergyStored"); }

		return super.getEnergyStored();
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) { return 0; }

		int energyExtracted = Math.min(getEnergyStored(), Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			getContainer().getOrCreateTagElement(NBT_KEY).putInt("EnergyStored", getEnergyStored() - energyExtracted);
		}
		return energyExtracted;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) { return 0; }

		int energyReceived = Math.min(capacity - getEnergyStored(), Math.min(this.maxReceive, maxReceive));
		if (!simulate) {
			getContainer().getOrCreateTagElement(NBT_KEY).putInt("EnergyStored", energyReceived + getEnergyStored());
		}
		return energyReceived;
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		return CapabilityEnergy.ENERGY.orEmpty(capability, holder);
	}

}
