package com.matyrobbrt.matytech.modules.robo_suit.module.installer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ModuleInstallerBlock extends Block {

	public ModuleInstallerBlock(Properties pros) {
		super(pros);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return super.createTileEntity(state, world);
	}

}
