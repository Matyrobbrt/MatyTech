package com.matyrobbrt.matytech.modules.eseparator;

import com.matyrobbrt.lib.annotation.SyncValue;
import com.matyrobbrt.lib.tile_entity.BaseTileEntity;
import com.matyrobbrt.matytech.api.capability.MTEnergyStorage;
import com.matyrobbrt.matytech.api.capability.MTFluidTank;
import com.matyrobbrt.matytech.api.tile.ISidedTickingTE;

import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class ESeparatorTileEntity extends BaseTileEntity implements ISidedTickingTE {

	@SyncValue(name = "energySync")
	public final MTEnergyStorage energyStorage = new MTEnergyStorage(0, 0, 0);
	private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

	@SyncValue(name = "fluidSync")
	public final MTFluidTank tank = new MTFluidTank(3, new int[] {
			12000, 12000, 30000
	});
	private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> tank);

	public ESeparatorTileEntity() {
		super(ElectrolyticSeparatorModule.ELECTROLYTIC_SEPARATOR_TE_TYPE);
	}

	@Override
	public World getTELevel() { return level; }

	@Override
	public void serverTick() {
		tank.fill(new FluidStack(ForgeMod.MILK.get(), 120), FluidAction.EXECUTE);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) { return energyOptional.cast(); }
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return fluidOptional.cast(); }
		return super.getCapability(cap, side);
	}
}
