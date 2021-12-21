package com.matyrobbrt.matytech.api.capability;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.matyrobbrt.lib.nbt.BaseNBTList;
import com.matyrobbrt.lib.util.helper.TernaryHelper;
import com.matyrobbrt.matytech.api.util.TransferValidator;
import com.matyrobbrt.matytech.api.util.TransferValidator.TransferType;

import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MTFluidTank implements IFluidHandler, INBTSerializable<CompoundNBT> {

	protected static final BiPredicate<Integer, FluidStack> DEFAULT_PREDICATE = (i, stack) -> true;
	protected static final TransferValidator DEFAULT_TRANSFER_VALIDATOR = (i, stack, type) -> stack.getAmount();

	protected BiPredicate<Integer, FluidStack> validator = DEFAULT_PREDICATE;
	protected TransferValidator transferValidator;
	@Nonnull
	protected BaseNBTList<FluidStack, CompoundNBT> fluids = new BaseNBTList<>(f -> f.writeToNBT(new CompoundNBT()),
			FluidStack::loadFluidStackFromNBT);
	protected final int tanks;
	protected int[] capacities;

	public MTFluidTank(int tanks, int[] capacities) {
		this(tanks, capacities, DEFAULT_TRANSFER_VALIDATOR);
	}

	public MTFluidTank(int tanks, int[] capacities, TransferValidator transferValidator) {
		this.tanks = tanks;
		this.capacities = capacities;
		this.transferValidator = transferValidator;
		IntStream.range(0, tanks).forEach(i -> fluids.add(FluidStack.EMPTY));
	}

	public MTFluidTank setValidator(BiPredicate<Integer, FluidStack> validator) {
		if (this.validator == null || this.validator == DEFAULT_PREDICATE) {
			this.validator = validator;
		}
		return this;
	}

	public MTFluidTank setTransfterValidator(TransferValidator transfterValidator) {
		if (this.transferValidator == null || this.validator == DEFAULT_TRANSFER_VALIDATOR) {
			this.transferValidator = transfterValidator;
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

		capacities = nbt.getIntArray("capacities").length != 0 ? nbt.getIntArray("capacities") : capacities;
	}

	public CompoundNBT serialize(CompoundNBT nbt) {
		nbt.put("fluids", fluids.serializeNBT());

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

	public int getTransfterLimit(int tank, FluidStack stack, TransferType type) {
		return transferValidator.getLimit(tank, stack, type);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		Optional<Pair<Integer, FluidStack>> stackOpt = getFluidForAnother(resource);
		if (!stackOpt.isPresent()) { return 0; }
		int index = stackOpt.get().getKey();
		return fillHelper(index, resource, action, TransferType.RECEIVE);
	}

	public int fill(int tank, FluidStack resource, FluidAction action) {
		return fillHelper(tank, resource, action, TransferType.RECEIVE);
	}

	public int fillInternally(int tank, FluidStack resource, FluidAction action) {
		return fillHelper(tank, resource, action, TransferType.RECEIVE_INTERNAL);
	}

	protected int fillHelper(int tank, FluidStack resource, FluidAction action, TransferType transferType) {
		if (tank + 1 > tanks) { return 0; }
		FluidStack stack = fluids.get(tank);
		if (resource.isEmpty() || !isFluidValid(tank, resource)) { return 0; }
		int toTransfter = getTransfterLimit(tank, resource, transferType);
		if (toTransfter > resource.getAmount()) {
			toTransfter = resource.getAmount();
		}
		if (action.simulate()) {
			if (stack.isEmpty()) { return Math.min(capacities[tank], toTransfter); }
			if (!stack.isFluidEqual(resource)) { return 0; }
			return Math.min(capacities[tank] - stack.getAmount(), toTransfter);
		}
		if (stack.isEmpty()) {
			fluids.set(tank, new FluidStack(resource, Math.min(capacities[tank], toTransfter)));
			onContentsChanged();
			return fluids.get(tank).getAmount();
		}
		if (!stack.isFluidEqual(resource)) { return 0; }
		int filled = capacities[tank] - stack.getAmount();

		if (toTransfter < filled) {
			stack.grow(toTransfter);
			filled = toTransfter;
		} else {
			stack.setAmount(capacities[tank]);
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
		int index = stackOpt.get().getKey();
		return drainHelper(index, resource, action, TransferType.EXTRACT);
	}

	@Nonnull
	public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
		return drainHelper(tank, resource, action, TransferType.EXTRACT);
	}

	@Nonnull
	public FluidStack drain(int tank, int amount, FluidAction action) {
		return drainHelper(tank, new FluidStack(getFluidInTank(tank), amount), action, TransferType.EXTRACT);
	}

	@Nonnull
	public FluidStack drainInternally(int tank, FluidStack resource, FluidAction action) {
		return drainHelper(tank, resource, action, TransferType.EXTRACT_INTERNAL);
	}

	@Nonnull
	public FluidStack drainInternally(int tank, int amount, FluidAction action) {
		return drainHelper(tank, new FluidStack(getFluidInTank(tank), amount), action, TransferType.EXTRACT_INTERNAL);
	}

	protected FluidStack drainHelper(int tank, FluidStack resource, FluidAction action, TransferType transferType) {
		if (tank + 1 > tanks) { return FluidStack.EMPTY; }
		FluidStack stack = fluids.get(tank);
		AtomicInteger transferLimit = new AtomicInteger(getTransfterLimit(tank, stack, transferType));
		if (transferLimit.get() > resource.getAmount()) {
			transferLimit.set(resource.getAmount());
		}
		if (resource.isEmpty()) { return FluidStack.EMPTY; }
		if (action.simulate()) {
			if (stack.isEmpty())
				return FluidStack.EMPTY;
			if (stack.getAmount() > transferLimit.get()) {
				return new FluidStack(stack.getFluid(), stack.getAmount() - transferLimit.get());
			} else {
				return new FluidStack(stack.getFluid(), 0);
			}
		}
		if (!stack.isEmpty()) {
			FluidStack toRet = TernaryHelper.supplier(() -> {
				if (stack.getAmount() >= transferLimit.get()) {
					return new FluidStack(stack.getFluid(), stack.getAmount() - transferLimit.get());
				} else {
					return new FluidStack(stack.getFluid(), 0);
				}
			});
			fluids.set(tank, toRet);
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
		int index = 1;
		for (int i = 0; i < tanks; i++) {
			if (!fluids.get(i).isEmpty()) {
				index = i;
				break;
			}
		}
		int drained = maxDrain;
		int limit = getTransfterLimit(index, fluids.get(index), TransferType.EXTRACT);
		if (limit < drained) {
			drained = limit;
		}
		if (fluids.get(index).getAmount() < drained) {
			drained = fluids.get(index).getAmount();
		}
		FluidStack stack = new FluidStack(fluids.get(index), drained);
		if (action.execute() && drained > 0) {
			fluids.get(index).shrink(drained);
			onContentsChanged();
		}
		return stack;
	}

	public void onContentsChanged() {

	}

}
