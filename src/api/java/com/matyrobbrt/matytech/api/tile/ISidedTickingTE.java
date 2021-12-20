package com.matyrobbrt.matytech.api.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.world.World;

public interface ISidedTickingTE extends ITickableTileEntity {

	World getTELevel();

	@Override
	default void tick() {
		if (getTELevel().isClientSide()) {
			clientTick();
		} else {
			serverTick();
		}
	}

	default void serverTick() {

	}

	default void clientTick() {

	}

}
