package com.matyrobbrt.matytech.modules.eseparator;

import com.matyrobbrt.matytech.api.container.MachineContainer;
import com.matyrobbrt.matytech.api.util.helper.InventoryHelper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ESeparatorContainer extends MachineContainer<ESeparatorTileEntity> {

	public ESeparatorContainer(final int windowId, final PlayerInventory playerInv, final ESeparatorTileEntity te) {
		super(ElectrolyticSeparatorModule.ELECTROLYTIC_SEPARATOR_CONTAINER_TYPE, windowId, te);

		InventoryHelper.createPlayerSlots(playerInv, 8, 84).forEach(this::addSlot);
	}

	public ESeparatorContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, getTileEntity(playerInv, data, ESeparatorTileEntity.class));
	}

	@Override
	public boolean stillValid(PlayerEntity p_75145_1_) {
		return stillValid(canInteractWithCallable, p_75145_1_,
				ElectrolyticSeparatorModule.ELECTROLYTIC_SEPARATOR_BLOCK);
	}

}
