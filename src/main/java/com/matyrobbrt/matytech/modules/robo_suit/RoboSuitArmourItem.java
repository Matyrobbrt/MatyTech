package com.matyrobbrt.matytech.modules.robo_suit;

import java.util.UUID;

import com.matyrobbrt.matytech.api.client.ClientRSMData;
import com.matyrobbrt.matytech.api.robo_suit.module.IRoboSuitModuleHolder;
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

public class RoboSuitArmourItem extends ArmorItem implements IRoboSuitModuleHolder {

	public static final IArmorMaterial MATERIAL = new BaseSpecialArmorMaterial() {

		@Override
		public String getName() { return "matytech:robo_suit"; }
	};

	public RoboSuitArmourItem(IArmorMaterial pMaterial, EquipmentSlotType pSlot, Properties pProperties) {
		super(pMaterial, pSlot, pProperties);
	}

	public static final String MODULE_INVENTORY_ID = "moduleInventoryUUID";

	@Override
	public UUID getStackModulesUUID(ItemStack stack) {
		return getStackUUID(stack);
	}

	public static UUID getStackUUID(final ItemStack stack) {
		if (stack.getOrCreateTag().contains(MODULE_INVENTORY_ID)) {
			return UUID.fromString(stack.getOrCreateTag().getString(MODULE_INVENTORY_ID));
		} else {
			UUID id = UUID.randomUUID();
			stack.getOrCreateTag().putString(MODULE_INVENTORY_ID, id.toString());
			return id;
		}
	}

	public static ModuleInventory getModules(final ItemStack stack, final MinecraftServer server) {
		RoboSuitModulesData data = RoboSuitModulesData.getDefaultInstance(server);
		return data.getOrCreateInventory(getStackUUID(stack));
	}

	@Override
	public void onArmorTick(ItemStack $stack, World world, PlayerEntity player) {
		if (world.isClientSide()) {
			ClientRSMData.getData().getOrCreateInventory(getStackUUID($stack)).getModules()
					.forEach(stack -> stack.onClientTick($stack, player));
		} else {
			getModules($stack, world.getServer()).getModules().forEach(stack -> stack.onServerTick($stack, player));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return "matytech:render/null_armor.png";
	}

}
