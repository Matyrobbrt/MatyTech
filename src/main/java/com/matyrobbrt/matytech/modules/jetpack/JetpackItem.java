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

import com.matyrobbrt.lib.MatyLib;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.lib.util.ColourCodes;
import com.matyrobbrt.lib.util.INamedEnum;
import com.matyrobbrt.lib.util.helper.NBTHelper;
import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.api.client.model.CustomArmourModel;
import com.matyrobbrt.matytech.api.item.IMode;
import com.matyrobbrt.matytech.api.item.IModeItem;
import com.matyrobbrt.matytech.api.item.IRenderableArmour;
import com.matyrobbrt.matytech.api.item.SpecialArmourMaterial;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

	public enum JetpackMode implements INamedEnum, IStringSerializable, IMode<JetpackMode> {

		NORMAL("normal"), HOVER("hover"), DISABLED("disabled");

		private final String name;

		private JetpackMode(String name) {
			this.name = name;
		}

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
