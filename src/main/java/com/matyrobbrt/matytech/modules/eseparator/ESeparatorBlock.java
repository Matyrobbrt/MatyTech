package com.matyrobbrt.matytech.modules.eseparator;

import com.matyrobbrt.lib.compat.top.ITOPDriver;
import com.matyrobbrt.lib.compat.top.ITOPInfoProvider;
import com.matyrobbrt.matytech.modules.eseparator.compat.ESeparatorTOPDriver;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

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
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit) {
		if (!worldIn.isClientSide()) {
			TileEntity te = worldIn.getBlockEntity(pos);
			if (te instanceof ESeparatorTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (ESeparatorTileEntity) te, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public ITOPDriver getTheOneProbeDriver() { return new ESeparatorTOPDriver(); }
}
