package com.matyrobbrt.matytech.modules.eseparator.compat;

import com.matyrobbrt.lib.compat.top.ITOPDriver;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;

public class ESeparatorTOPDriver implements ITOPDriver {

	@Override
	public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, PlayerEntity player, World level,
			BlockState blockState, IProbeHitData probeData) {

	}

}
