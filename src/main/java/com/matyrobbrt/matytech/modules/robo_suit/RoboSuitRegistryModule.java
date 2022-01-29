package com.matyrobbrt.matytech.modules.robo_suit;

import com.matyrobbrt.lib.annotation.RL;
import com.matyrobbrt.lib.module.IModule;
import com.matyrobbrt.lib.module.Module;
import com.matyrobbrt.lib.registry.annotation.RegisterBlock;
import com.matyrobbrt.lib.registry.annotation.RegisterItem;
import com.matyrobbrt.lib.registry.annotation.RegisterTileEntityType;
import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.api.annotation.RegisterRoboSuitModule;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModule;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModule.Properties;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModuleStack;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModulesData;
import com.matyrobbrt.matytech.modules.robo_suit.module.installer.ModuleInstallerBlock;
import com.matyrobbrt.matytech.modules.robo_suit.module.installer.ModuleInstallerTileEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Module(id = @RL(modid = MatyTech.MOD_ID, path = "robo_suit"))
public class RoboSuitRegistryModule implements IModule {

	@RegisterBlock("module_installer")
	public static final ModuleInstallerBlock MODULE_INSTALLER_BLOCK = new ModuleInstallerBlock(
			AbstractBlock.Properties.of(Material.HEAVY_METAL));

	@RegisterTileEntityType("module_installer")
	public static final TileEntityType<ModuleInstallerTileEntity> MODULE_INSTALLER_TE_TYPE = TileEntityType.Builder
			.of(ModuleInstallerTileEntity::new, MODULE_INSTALLER_BLOCK).build(null);

	@RegisterRoboSuitModule("test")
	public static final RoboSuitModule TEST_MODULE = new RoboSuitModule(new Properties().setStackSize(16)) {

		@Override
		public void onServerTick(RoboSuitModuleStack moduleStack, ItemStack stack, PlayerEntity player) {
			System.out.println("server tick");
		}
	};

	@RegisterItem("test_robo_suit_chestplate")
	public static final RoboSuitArmourItem TEST_ROBO_SUIT_CHESPLATE = new RoboSuitArmourItem(
			RoboSuitArmourItem.MATERIAL, EquipmentSlotType.CHEST, new Item.Properties().tab(MatyTech.MATY_TECH_TAB)) {

		@Override
		public net.minecraft.util.ActionResult<ItemStack> use(World pLevel, PlayerEntity pPlayer, Hand pHand) {

			if (!pLevel.isClientSide()) {
				RoboSuitModulesData.getDefaultInstance(pLevel).addModule(getStackUUID(pPlayer.getItemInHand(pHand)),
						new RoboSuitModuleStack(TEST_MODULE, 3), pPlayer.getItemInHand(pHand), false);
			}
			return super.use(pLevel, pPlayer, pHand);
		}
	};

	@RegisterItem("robo_suit_helmet")
	public static final RoboSuitArmourItem ROBO_SUIT_HELMET = new RoboSuitArmourItem(RoboSuitArmourItem.MATERIAL,
			EquipmentSlotType.HEAD, new Item.Properties().tab(MatyTech.MATY_TECH_TAB));
	@RegisterItem("robo_suit_chestplate")
	public static final RoboSuitArmourItem ROBO_SUIT_CHESTPLATE = new RoboSuitArmourItem(RoboSuitArmourItem.MATERIAL,
			EquipmentSlotType.CHEST, new Item.Properties().tab(MatyTech.MATY_TECH_TAB));
	@RegisterItem("robo_suit_leggings")
	public static final RoboSuitArmourItem ROBO_SUIT_LEGGINGS = new RoboSuitArmourItem(RoboSuitArmourItem.MATERIAL,
			EquipmentSlotType.LEGS, new Item.Properties().tab(MatyTech.MATY_TECH_TAB));
	@RegisterItem("robo_suit_boots")
	public static final RoboSuitArmourItem ROBO_SUIT_BOOTS = new RoboSuitArmourItem(RoboSuitArmourItem.MATERIAL,
			EquipmentSlotType.FEET, new Item.Properties().tab(MatyTech.MATY_TECH_TAB));

}
