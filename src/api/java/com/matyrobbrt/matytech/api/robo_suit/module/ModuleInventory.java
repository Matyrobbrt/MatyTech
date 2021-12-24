package com.matyrobbrt.matytech.api.robo_suit.module;

import java.util.List;

import com.matyrobbrt.lib.nbt.NBTList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.common.util.INBTSerializable;

public class ModuleInventory implements INBTSerializable<CompoundNBT> {

	private NBTList<RoboSuitModuleStack, CompoundNBT> modules = new NBTList<>(RoboSuitModuleStack::fromNBT);

	public List<RoboSuitModuleStack> getModules() { return modules; }

	public RoboSuitModuleStack addModule(RoboSuitModuleStack stack, ItemStack container, PlayerEntity player,
			boolean simulate) {
		boolean addedToStack = false;
		RoboSuitModuleStack toReturn = stack.copy();
		for (RoboSuitModuleStack module : modules) {
			if (module.canStack(stack)) {
				int toAdd = Math.min(module.getMaxStackSize() - module.getCount(), stack.getCount());
				if (toAdd > 0) {
					if (!simulate) {
						module.grow(toAdd);
						module.getModule().onAdded(module, container, player);
					}
					toReturn.shrink(toAdd);
					addedToStack = true;
					break;
				}
			}
		}
		if (!addedToStack && !hasModule(stack.getModule())) {
			modules.add(stack);
			toReturn = RoboSuitModuleStack.EMPTY;
		}
		return toReturn;
	}

	public boolean hasModule(RoboSuitModule moduleType) {
		return modules.stream().anyMatch(s -> s.getModule() == moduleType);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		modules.deserializeNBT(nbt.getCompound("modules"));
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("modules", modules.serializeNBT());
		return nbt;
	}

	public static ModuleInventory fromNBT(CompoundNBT nbt) {
		ModuleInventory inv = new ModuleInventory();
		inv.deserializeNBT(nbt);
		return inv;
	}

}
