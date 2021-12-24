package com.matyrobbrt.matytech.modules.robo_suit;

import java.util.UUID;

import com.matyrobbrt.matytech.api.robo_suit.module.ModuleInventory;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModulesData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import mekanism.common.item.gear.BaseSpecialArmorMaterial;

public class RoboSuitArmourItem extends ArmorItem {

	public static final IArmorMaterial MATERIAL = new BaseSpecialArmorMaterial() {

		@Override
		public String getName() { return "matytech:robo_suit"; }
	};

	public RoboSuitArmourItem(IArmorMaterial pMaterial, EquipmentSlotType pSlot, Properties pProperties) {
		super(pMaterial, pSlot, pProperties);
	}

	public static final String MODULE_INVENTORY_ID = "moduleInventoryUUID";

	public static UUID getStackUUID(final ItemStack stack) {
		if (stack.getOrCreateTag().hasUUID(MODULE_INVENTORY_ID)) {
			return stack.getOrCreateTag().getUUID(MODULE_INVENTORY_ID);
		} else {
			UUID id = UUID.randomUUID();
			stack.getOrCreateTag().putUUID(MODULE_INVENTORY_ID, id);
			return id;
		}
	}

	public static ModuleInventory getModules(final ItemStack stack, final MinecraftServer server) {
		RoboSuitModulesData data = RoboSuitModulesData.getDefaultInstance(server);
		return data.getOrCreateInventory(getStackUUID(stack));
	}

	@Override
	public void onArmorTick(ItemStack $stack, World world, PlayerEntity player) {
		if (!world.isClientSide()) {
			getModules($stack, world.getServer()).getModules().forEach(stack -> stack.onClientTick($stack, player));
		} else {
			getModules($stack, world.getServer()).getModules().forEach(stack -> stack.onServerTick($stack, player));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return "matytech:render/null_armor.png";
	}

}
