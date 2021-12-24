package com.matyrobbrt.matytech.api.robo_suit.module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.INBTSerializable;

public class RoboSuitModuleStack implements INBTSerializable<CompoundNBT> {

	public static final RoboSuitModuleStack EMPTY = new RoboSuitModuleStack(RoboSuitModule.AIR, 0);

	private final RoboSuitModule module;
	private int count;
	private CompoundNBT tag;

	public RoboSuitModuleStack(RoboSuitModule module) {
		this(module, 1);
	}

	public RoboSuitModuleStack(RoboSuitModule module, int count) {
		this(module, count, null);
	}

	public RoboSuitModuleStack(RoboSuitModule module, int count, CompoundNBT tag) {
		this.module = module;
		setCount(count);
		setTag(tag);
	}

	public RoboSuitModule getModule() { return module; }

	public int getCount() { return count; }

	public void setCount(int count) {
		if (count <= getModule().getMaxStackSize()) {
			this.count = count;
		}
	}

	public CompoundNBT getTag() { return tag; }

	public boolean hasTag() {
		return tag != null;
	}

	@Nonnull
	public CompoundNBT getOrCreateTag() {
		if (tag == null) {
			this.setTag(new CompoundNBT());
		}
		return tag;
	}

	public void setTag(@Nullable CompoundNBT tag) { this.tag = tag; }

	public boolean isEmpty() {
		if (this == EMPTY || getModule() == null || getModule() == RoboSuitModule.AIR) {
			return true;
		} else {
			return count <= 0;
		}
	}

	public boolean canStack(@Nonnull RoboSuitModuleStack other) {
		if (isEmpty() || !this.sameModule(other) || hasTag() != other.hasTag()) { return false; }

		return !hasTag() || getTag().equals(other.getTag());
	}

	public boolean sameModule(@Nonnull RoboSuitModuleStack other) {
		return other.getModule() == getModule();
	}

	public RoboSuitModuleStack copy() {
		return fromNBT(serializeNBT());
	}

	public int getMaxStackSize() { return getModule().getMaxStackSize(); }

	public void grow(int pIncrement) {
		this.setCount(this.count + pIncrement);
	}

	public void shrink(int pDecrement) {
		this.grow(-pDecrement);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("module", module.getRegistryName().toString());
		nbt.putInt("count", count);
		if (tag != null) {
			nbt.put("nbt", tag);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setCount(nbt.getInt("count"));
		if (nbt.contains("nbt")) {
			setTag(nbt.getCompound("nbt"));
		}
	}

	public void onServerTick(final ItemStack gearStack, final PlayerEntity player) {
		getModule().onServerTick(this, gearStack, player);
	}

	public void onClientTick(final ItemStack gearStack, final PlayerEntity player) {
		getModule().onClientTick(this, gearStack, player);
	}

	public static RoboSuitModuleStack fromNBT(CompoundNBT nbt) {
		RoboSuitModuleStack stack = new RoboSuitModuleStack(
				RoboSuitModule.REGISTRY.getValue(new ResourceLocation(nbt.getString("module"))));
		stack.deserializeNBT(nbt);
		return stack;
	}
}
