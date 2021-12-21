package com.matyrobbrt.matytech.api.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class BaseContainer extends Container {

	protected BaseContainer(ContainerType<?> pMenuType, int pContainerId) {
		super(pMenuType, pContainerId);
	}

	@Override
	public boolean stillValid(PlayerEntity pPlayer) {
		return false;
	}

}
