package com.matyrobbrt.matytech.api.client.screen;

import com.matyrobbrt.matytech.api.container.MachineContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MachineContainerScreen<C extends MachineContainer<?>> extends BaseContainerScreen<C> {

	public MachineContainerScreen(C pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle,
			ResourceLocation bgTexture) {
		super(pMenu, pPlayerInventory, pTitle, bgTexture);
	}

}
