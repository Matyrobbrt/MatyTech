package com.matyrobbrt.matytech.api.container;

import java.util.Objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class MachineContainer<T extends TileEntity> extends BaseContainer {

	public final T tile;

	protected final IWorldPosCallable canInteractWithCallable;

	protected MachineContainer(ContainerType<?> pMenuType, int pContainerId, T tile) {
		super(pMenuType, pContainerId);
		this.tile = tile;
		this.canInteractWithCallable = IWorldPosCallable.create(tile.getLevel(), tile.getBlockPos());
	}

	protected static <T extends TileEntity> T getTileEntity(final PlayerInventory playerInv, final PacketBuffer data,
			Class<T> tileClass) {
		Objects.requireNonNull(playerInv, "Player Inventory cannot be null.");
		Objects.requireNonNull(data, "Packet Buffer cannot be null.");
		final TileEntity te = playerInv.player.level.getBlockEntity(data.readBlockPos());
		if (tileClass.isInstance(te)) { return tileClass.cast(te); }
		throw new IllegalStateException("Tile Entity Is Not Correct");
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity pPlayer, int pIndex) {
		return ItemStack.EMPTY;
	}

}
