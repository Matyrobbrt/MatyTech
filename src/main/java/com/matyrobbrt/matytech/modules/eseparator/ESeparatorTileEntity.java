package com.matyrobbrt.matytech.modules.eseparator;

import com.matyrobbrt.lib.annotation.SyncValue;
import com.matyrobbrt.lib.network.BaseNetwork;
import com.matyrobbrt.lib.network.matylib.SyncValuesMessage;
import com.matyrobbrt.lib.tile_entity.BaseTileEntity;
import com.matyrobbrt.matytech.api.capability.MTEnergyStorage;
import com.matyrobbrt.matytech.api.capability.MTFluidTank;
import com.matyrobbrt.matytech.api.tile.IFluidInventory;
import com.matyrobbrt.matytech.api.tile.ISidedTickingTE;
import com.matyrobbrt.matytech.api.util.TransferValidator.TransferType;
import com.matyrobbrt.matytech.network.MTNetwork;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ESeparatorTileEntity extends BaseTileEntity
		implements ISidedTickingTE, IFluidInventory, INamedContainerProvider {

	@SyncValue(name = "energySync", onPacket = true)
	public final MTEnergyStorage energyStorage = new MTEnergyStorage(120000, 1300, 0) {

		@Override
		public void setChanged() {
			ESeparatorTileEntity.this.setChanged();
		}
	};
	private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

	/**
	 * Left is 1; Right is 2; Input is 0
	 */
	@SyncValue(name = "fluidSync", onPacket = true)
	public final MTFluidTank tank = new MTFluidTank(3, new int[] {
			30000, 12000, 12000
	}, (i, stack, type) -> {
		if (type == TransferType.EXTRACT_INTERNAL) { return stack.getAmount(); }
		if (type == TransferType.RECEIVE && i != 0) { return 0; }
		if (type == TransferType.EXTRACT) { return i == 0 ? 0 : stack.getAmount(); }
		return stack.getAmount();
	}) {

		@Override
		public void onContentsChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> tank);

	public ESeparatorTileEntity() {
		super(ElectrolyticSeparatorModule.ELECTROLYTIC_SEPARATOR_TE_TYPE);
	}

	@Override
	public World getTELevel() { return level; }

	@SyncValue(name = "progressSync", onPacket = true)
	private int progress;

	@Override
	public void serverTick() {
		if (getRecipe() != null) {
			if (energyStorage.getEnergyStored() >= 300) {
				progress++;
				ESeparatorRecipe cachedRecipe = getRecipe();
				if (progress >= cachedRecipe.getProcessTime()) {
					int amount = cachedRecipe.getInputFluid().getMatchingInstance(tank.getFluidInTank(0)).getAmount();
					tank.drainInternally(0, amount, FluidAction.EXECUTE).getAmount();
					if (!cachedRecipe.getOutput1().isEmpty()) {
						tank.fillInternally(1, cachedRecipe.getOutput1(), FluidAction.EXECUTE);
					}
					if (!cachedRecipe.getOutput2().isEmpty()) {
						tank.fillInternally(2, cachedRecipe.getOutput2(), FluidAction.EXECUTE);
					}
					setChanged();
					progress = 0;
				}
				energyStorage.extractInternalEnergy(300, false);
			}
		}
	}

	public ESeparatorRecipe getRecipe() {
		if (level == null) { return null; }
		return level.getRecipeManager().getRecipeFor(ESeparatorRecipe.RECIPE_TYPE, this, level).orElse(null);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) { return energyOptional.cast(); }
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return fluidOptional.cast(); }
		return super.getCapability(cap, side);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		sync(com.matyrobbrt.lib.network.matylib.SyncValuesMessage.Direction.SERVER_TO_CLIENT);
	}

	@Override
	public void sync(com.matyrobbrt.lib.network.matylib.SyncValuesMessage.Direction direction) {
		if (direction == com.matyrobbrt.lib.network.matylib.SyncValuesMessage.Direction.SERVER_TO_CLIENT) {
			BaseNetwork.sendToAllTracking(MTNetwork.TILE_SYNCING, new SyncValuesMessage(getBlockPos(), this, direction),
					this);
		} else {
			MTNetwork.TILE_SYNCING.sendToServer(new SyncValuesMessage(this.getBlockPos(), this, direction));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT $t) {
		CompoundNBT nbt = super.save($t);
		nbt.put("energy", energyStorage.serializeNBT());
		nbt.put("tank", tank.serializeNBT());
		nbt.putInt("progress", progress);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		energyStorage.deserializeNBT(nbt.getCompound("energy"));
		tank.deserializeNBT(nbt.getCompound("tank"));
		progress = nbt.getInt("progress");
	}

	@Override
	public MTFluidTank getFluidHandler() { return tank; }

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new ESeparatorContainer(p_createMenu_1_, p_createMenu_2_, this);
	}

	@Override
	public ITextComponent getDisplayName() { return new StringTextComponent(""); }
}
