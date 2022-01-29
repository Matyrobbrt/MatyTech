package com.matyrobbrt.matytech.modules.robo_suit.module.installer;

import com.matyrobbrt.lib.tile_entity.BaseTileEntity;
import com.matyrobbrt.matytech.api.robo_suit.module.IRoboSuitModuleHolder;
import com.matyrobbrt.matytech.api.robo_suit.module.IRoboSuitModuleItem;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModuleStack;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModulesData;
import com.matyrobbrt.matytech.api.tile.ISidedTickingTE;
import com.matyrobbrt.matytech.modules.robo_suit.RoboSuitRegistryModule;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ModuleInstallerTileEntity extends BaseTileEntity implements ISidedTickingTE {

	public final ItemStackHandler inventory = new ItemStackHandler(2);
	private final LazyOptional<IItemHandler> invOptional = LazyOptional.of(() -> inventory);

	public ModuleInstallerTileEntity() {
		super(RoboSuitRegistryModule.MODULE_INSTALLER_TE_TYPE);
	}

	@Override
	public void serverTick() {
		handleModuleTransfer();
	}

	private void handleModuleTransfer() {
		if (inventory.getStackInSlot(0).isEmpty() || inventory.getStackInSlot(1).isEmpty()) { return; }
		if (!(inventory.getStackInSlot(0).getItem() instanceof IRoboSuitModuleHolder)
				|| !(inventory.getStackInSlot(1).getItem() instanceof IRoboSuitModuleItem)) {
			return;
		}

		IRoboSuitModuleHolder moduleHolder = (IRoboSuitModuleHolder) inventory.getStackInSlot(0).getItem();
		IRoboSuitModuleItem moduleItem = (IRoboSuitModuleItem) inventory.getStackInSlot(1).getItem();
		RoboSuitModulesData data = RoboSuitModulesData.getDefaultInstance(level);

		ItemStack gear = inventory.getStackInSlot(0);
		RoboSuitModuleStack moduleStack = moduleItem.getModule(inventory.getStackInSlot(1));

		if (data.addModule(moduleHolder.getStackModulesUUID(gear), moduleStack, gear, true).getCount() != moduleStack
				.getCount()) {
			inventory.getStackInSlot(1).setCount(
					data.addModule(moduleHolder.getStackModulesUUID(gear), moduleStack, gear, false).getCount());
		}
	}

	@Override
	public World getTELevel() { return level; }

	@Override
	public CompoundNBT save(CompoundNBT tags) {
		CompoundNBT nbt = super.save(tags);
		nbt.put("inventory", inventory.serializeNBT());
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return invOptional.cast(); }
		return super.getCapability(cap, side);
	}
}
