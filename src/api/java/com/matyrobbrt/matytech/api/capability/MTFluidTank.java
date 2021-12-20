package com.matyrobbrt.matytech.api.capability;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.matyrobbrt.lib.nbt.BaseNBTList;
import com.matyrobbrt.lib.util.helper.TernaryHelper;

import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MTFluidTank implements IFluidHandler, INBTSerializable<CompoundNBT> {

	protected static final BiPredicate<Integer, FluidStack> DEFAULT_PREDICATE = (i, stack) -> true;

	protected BiPredicate<Integer, FluidStack> validator = DEFAULT_PREDICATE;
	@Nonnull
	protected BaseNBTList<FluidStack, CompoundNBT> fluids = new BaseNBTList<>(f -> f.writeToNBT(new CompoundNBT()),
			FluidStack::loadFluidStackFromNBT);
	protected final int tanks;
	protected int[] capacities;

	public MTFluidTank(int tanks, int[] capacities) {
		this.tanks = tanks;
		this.capacities = capacities;
		IntStream.range(0, tanks).forEach(i -> fluids.add(FluidStack.EMPTY));
	}

	public MTFluidTank setValidator(BiPredicate<Integer, FluidStack> validator) {
		if (this.validator == null || this.validator == DEFAULT_PREDICATE) {
			this.validator = validator;
		}
		return this;
	}

	public static MTFluidTank fromNBT(CompoundNBT nbt) {
		MTFluidTank tank = new MTFluidTank(nbt.getInt("tanks"), nbt.getIntArray("capacities"));
		tank.deserializeNBT(nbt);
		return tank;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		fluids.deserializeNBT(nbt.getCompound("fluids"));
		capacities = nbt.getIntArray("capacities");
	}

	public CompoundNBT serialize(CompoundNBT nbt) {
		nbt.put("fludis", fluids.serializeNBT());

		nbt.putInt("tanks", tanks);
		nbt.putIntArray("capacities", capacities);
		return nbt;
	}

	@Override
	public CompoundNBT serializeNBT() {
		return serialize(new CompoundNBT());
	}

	@Override
	public int getTanks() { return tanks; }

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return fluids.get(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return capacities[tank];
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return validator.test(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		Optional<Pair<Integer, FluidStack>> stackOpt = getFluidForAnother(resource);
		if (!stackOpt.isPresent()) { return 0; }
		FluidStack stack = stackOpt.get().getRight();
		int index = stackOpt.get().getKey();
		if (resource.isEmpty() || !isFluidValid(index, resource)) { return 0; }
		if (action.simulate()) {
			if (stack.isEmpty()) { return Math.min(capacities[index], resource.getAmount()); }
			if (!stack.isFluidEqual(resource)) { return 0; }
			return Math.min(capacities[index] - stack.getAmount(), resource.getAmount());
		}
		if (stack.isEmpty()) {
			fluids.set(index, new FluidStack(resource, Math.min(capacities[index], resource.getAmount())));
			onContentsChanged();
			return fluids.get(index).getAmount();
		}
		if (!stack.isFluidEqual(resource)) { return 0; }
		int filled = capacities[index] - stack.getAmount();

		if (resource.getAmount() < filled) {
			stack.grow(resource.getAmount());
			filled = resource.getAmount();
		} else {
			stack.setAmount(capacities[index]);
		}
		if (filled > 0) {
			onContentsChanged();
		}
		return filled;
	}

	public Optional<Pair<Integer, FluidStack>> getFluidForAnother(FluidStack other) {
		for (int i = 0; i < fluids.size(); i++) {
			if (fluids.get(i).isFluidEqual(other) || fluids.get(i).isEmpty()) {
				return Optional.of(Pair.of(i, fluids.get(i)));
			}
		}
		return Optional.empty();
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		Optional<Pair<Integer, FluidStack>> stackOpt = getFluidForAnother(resource);
		if (!stackOpt.isPresent()) { return FluidStack.EMPTY; }
		FluidStack stack = stackOpt.get().getRight();
		int index = stackOpt.get().getKey();
		if (resource.isEmpty() || !isFluidValid(index, resource)) { return FluidStack.EMPTY; }
		if (action.simulate()) {
			if (stack.isEmpty())
				return FluidStack.EMPTY;
			if (stack.getAmount() > resource.getAmount()) {
				return new FluidStack(stack.getFluid(), resource.getAmount());
			} else {
				return new FluidStack(stack.getFluid(), stack.getAmount());
			}
		}
		if (!stack.isEmpty()) {
			FluidStack toRet = TernaryHelper.supplier(() -> {
				if (stack.getAmount() > resource.getAmount()) {
					return new FluidStack(stack.getFluid(), resource.getAmount());
				} else {
					return new FluidStack(stack.getFluid(), stack.getAmount());
				}
			});
			fluids.set(index, toRet);
			onContentsChanged();
			return toRet;
		}
		return FluidStack.EMPTY;
	}

	/**
	 * @deprecated Use
	 *             {@link #drain(FluidStack, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction)}
	 */
	@Deprecated
	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		int drained = maxDrain;
		if (fluids.get(0).getAmount() < drained) {
			drained = fluids.get(0).getAmount();
		}
		FluidStack stack = new FluidStack(fluids.get(0), drained);
		if (action.execute() && drained > 0) {
			fluids.get(0).shrink(drained);
			onContentsChanged();
		}
		return stack;
	}

	public void onContentsChanged() {

	}

}
