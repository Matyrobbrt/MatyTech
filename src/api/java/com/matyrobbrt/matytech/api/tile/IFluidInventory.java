package com.matyrobbrt.matytech.api.tile;

import com.matyrobbrt.matytech.api.capability.MTFluidTank;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidInventory extends IInventory, IFluidHandler {

	@Override
	default void clearContent() {
	}

	@Override
	default int getContainerSize() { return 0; }

	@Override
	default ItemStack getItem(int pIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean isEmpty() { return false; }

	@Override
	default ItemStack removeItem(int pIndex, int pCount) {
		return ItemStack.EMPTY;
	}

	@Override
	default ItemStack removeItemNoUpdate(int pIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	default void setItem(int pIndex, ItemStack pStack) {
	}

	@Override
	default boolean stillValid(PlayerEntity pPlayer) {
		return false;
	}

	MTFluidTank getFluidHandler();

	@Override
	default FluidStack drain(FluidStack resource, FluidAction action) {
		return getFluidHandler().drain(resource, action);
	}

	@SuppressWarnings("deprecation")
	@Override
	default FluidStack drain(int maxDrain, FluidAction action) {
		return getFluidHandler().drain(maxDrain, action);
	}

	@Override
	default int fill(FluidStack resource, FluidAction action) {
		return getFluidHandler().fill(resource, action);
	}

	default int fillInternally(int tank, FluidStack stack, FluidAction action) {
		return getFluidHandler().fillInternally(tank, stack, action);
	}

	@Override
	default FluidStack getFluidInTank(int tank) {
		return getFluidHandler().getFluidInTank(tank);
	}

	@Override
	default int getTankCapacity(int tank) {
		return getFluidHandler().getTankCapacity(tank);
	}

	@Override
	default int getTanks() { return getFluidHandler().getTanks(); }

	@Override
	default boolean isFluidValid(int tank, FluidStack stack) {
		return getFluidHandler().isFluidValid(tank, stack);
	}
}
