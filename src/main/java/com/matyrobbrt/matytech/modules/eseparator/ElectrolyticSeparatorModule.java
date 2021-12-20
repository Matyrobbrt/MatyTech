package com.matyrobbrt.matytech.modules.eseparator;

import com.matyrobbrt.lib.annotation.RL;
import com.matyrobbrt.lib.module.IModule;
import com.matyrobbrt.lib.module.Module;
import com.matyrobbrt.lib.registry.annotation.AutoBlockItem;
import com.matyrobbrt.lib.registry.annotation.RegisterBlock;
import com.matyrobbrt.lib.registry.annotation.RegisterTileEntityType;
import com.matyrobbrt.matytech.MatyTech;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;

@Module(id = @RL(modid = MatyTech.MOD_ID, path = "electrolytic_separator"))
public class ElectrolyticSeparatorModule implements IModule {

	@AutoBlockItem
	@RegisterBlock("electrolytic_separator")
	public static final ESeparatorBlock ELECTROLYTIC_SEPARATOR_BLOCK = new ESeparatorBlock(
			AbstractBlock.Properties.of(Material.HEAVY_METAL));

	@RegisterTileEntityType("electrolytic_separator")
	public static final TileEntityType<ESeparatorTileEntity> ELECTROLYTIC_SEPARATOR_TE_TYPE = TileEntityType.Builder
			.of(ESeparatorTileEntity::new, ELECTROLYTIC_SEPARATOR_BLOCK).build(null);

}
