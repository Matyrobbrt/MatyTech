/**
 * This file is part of the MatyTech Minecraft (Java Edition) mod and is
 * licensed under the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2021 Matyrobbrt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.matyrobbrt.matytech.modules.jetpack;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.matyrobbrt.lib.MatyLib;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.lib.util.ColourCodes;
import com.matyrobbrt.lib.util.INamedEnum;
import com.matyrobbrt.lib.util.helper.NBTHelper;
import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.api.capability.EnergyStorageItemStack;
import com.matyrobbrt.matytech.api.client.model.CustomArmourModel;
import com.matyrobbrt.matytech.api.item.IMode;
import com.matyrobbrt.matytech.api.item.IModeItem;
import com.matyrobbrt.matytech.api.item.IRenderableArmour;
import com.matyrobbrt.matytech.api.item.SpecialArmourMaterial;
import com.matyrobbrt.matytech.init.TagInit;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

@RegistryHolder(modid = MatyTech.MOD_ID)
public class JetpackItem extends ArmorItem
		implements IRenderableArmour, IModeItem<com.matyrobbrt.matytech.modules.jetpack.JetpackItem.JetpackMode> {

	private static final SpecialArmourMaterial MATERIAL = new SpecialArmourMaterial() {

		@Override
		public String getName() { return MatyLib.MOD_ID + ":jetpack"; }
	};

	public JetpackItem(EquipmentSlotType pSlot, Properties pProperties) {
		super(MATERIAL, pSlot, pProperties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public CustomArmourModel getModel(ItemStack stack) {
		return JetpackRender.RENDERER;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return "matytech:render/null_armor.png";
	}

	public static JetpackMode getMode(ItemStack stack) {
		JetpackMode mode = JetpackMode.getValueOf(NBTHelper.getString(stack, "JetpackMode"));
		return mode == null ? JetpackMode.NORMAL : mode;
	}

	@Override
	public JetpackMode setStackMode(PlayerEntity player, ItemStack stack, JetpackMode mode) {
		return setMode(stack, mode);
	}

	@Override
	public JetpackMode getStackMode(ItemStack stack) {
		return getMode(stack);
	}

	@Override
	public JetpackMode cycleMode(PlayerEntity player, ItemStack stack) {
		JetpackMode mode = IModeItem.super.cycleMode(player, stack);
		if (player != null) {
			player.sendMessage(new StringTextComponent("Changed Jetpack mode to " + ColourCodes.GOLD + mode.toString()),
					net.minecraft.util.Util.NIL_UUID);
		}
		return mode;
	}

	public static JetpackMode setMode(ItemStack stack, JetpackMode mode) {
		NBTHelper.setString(stack, "JetpackMode", mode.getName());
		return mode;
	}

	public static boolean hasFuelForFlight(ItemStack stack, JetpackMode mode) {
		AtomicBoolean value = new AtomicBoolean(false);

		stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cap -> {
			FluidStack fluid = cap.getFluidInTank(0);
			if (!fluid.isEmpty() && fluid.getFluid().is(TagInit.Fluids.HYDROGEN)
					&& fluid.getAmount() >= mode.getFuelPerTick()) {
				value.set(true);
			}
		});

		return value.get();
	}

	public static boolean consumeFuel(ItemStack stack, JetpackMode mode) {
		boolean hasFuel = hasFuelForFlight(stack, mode);

		if (hasFuel) {
			stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cap -> {
				FluidStack fluid = cap.getFluidInTank(0);
				cap.drain(new FluidStack(fluid.getFluid(), mode.getFuelPerTick()), FluidAction.EXECUTE);
			});
		}

		return hasFuel;
	}

	private static final int ENERGY_CONSUMED = 200;

	public static boolean hasEnergyForFall(ItemStack stack) {
		if (!(stack.getItem() instanceof JetpackItem)) { return false; }

		AtomicBoolean value = new AtomicBoolean(false);
		stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(cap -> {
			if (cap.getEnergyStored() >= ENERGY_CONSUMED) {
				value.set(true);
			}
		});
		return value.get();
	}

	public static boolean consumeFallEnergy(ItemStack stack) {
		boolean hasEnergy = hasEnergyForFall(stack);

		if (hasEnergy) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(cap -> {
				if (cap.getEnergyStored() >= ENERGY_CONSUMED) {
					System.out.println(cap.extractEnergy(ENERGY_CONSUMED, false));
				}
			});
		}

		return hasEnergy;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new ICapabilityProvider() {

			private final FluidHandlerItemStack fluid = new FluidHandlerItemStack(stack, 12000) {

				@Override
				public boolean isFluidValid(int tank, FluidStack stack) {
					return stack.getFluid().is(TagInit.Fluids.HYDROGEN);
				}
			};

			private final LazyOptional<IFluidHandlerItem> fluidOptional = LazyOptional.of(() -> fluid);

			private final EnergyStorageItemStack energy = new EnergyStorageItemStack(stack, 12000, 12000, 12000);

			private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energy);

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
					return fluidOptional.cast();
				} else if (cap == CapabilityEnergy.ENERGY) { return energyOptional.cast(); }

				return LazyOptional.empty();
			}
		};
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack pStack, World pLevel, List<ITextComponent> pTooltip, ITooltipFlag pFlag) {
		pStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cap -> {
			pTooltip.add(new StringTextComponent("Contains Fluid " + cap.getFluidInTank(0).getAmount()));
		});
		pStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(cap -> {
			pTooltip.add(new StringTextComponent("Contains Enegy " + cap.getEnergyStored()));
		});

		pTooltip.add(new StringTextComponent("Hover " + JetpackMode.HOVER.getFuelPerTick()));
		pTooltip.add(new StringTextComponent("Normal " + JetpackMode.NORMAL.getFuelPerTick()));
	}

	public enum JetpackMode implements INamedEnum, IStringSerializable, IMode<JetpackMode> {

		NORMAL("normal", JetpackModule::getHydrogenNormalPerTick),
		HOVER("hover", JetpackModule::getHydrogenHoverPerTick), DISABLED("disabled", () -> 0);

		private final String name;
		private final Supplier<Integer> fuelPerTick;

		private JetpackMode(String name, Supplier<Integer> fuelPerTick) {
			this.name = name;
			this.fuelPerTick = fuelPerTick;
		}

		public int getFuelPerTick() { return fuelPerTick.get(); }

		@Override
		public String getName() { return name; }

		@Override
		public String getSerializedName() { return name; }

		public static JetpackMode getValueOf(String name) {
			for (JetpackMode mode : values()) {
				if (name == mode.name) { return mode; }
			}
			return null;
		}

		@Override
		public JetpackMode next() {
			switch (this) {
			case NORMAL:
				return HOVER;
			case HOVER:
				return DISABLED;
			case DISABLED:
				return NORMAL;
			default:
				return DISABLED;
			}
		}
	}

}
