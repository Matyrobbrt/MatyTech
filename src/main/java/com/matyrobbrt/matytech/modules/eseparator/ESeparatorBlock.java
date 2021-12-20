package com.matyrobbrt.matytech.modules.eseparator;

import com.matyrobbrt.lib.compat.top.ITOPDriver;
import com.matyrobbrt.lib.compat.top.ITOPInfoProvider;
import com.matyrobbrt.matytech.modules.eseparator.compat.ESeparatorTOPDriver;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ESeparatorBlock extends Block implements ITOPInfoProvider {

	public ESeparatorBlock(Properties props) {
		super(props);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ESeparatorTileEntity();
	}

	@Override
	public ITOPDriver getTheOneProbeDriver() { return new ESeparatorTOPDriver(); }
}
