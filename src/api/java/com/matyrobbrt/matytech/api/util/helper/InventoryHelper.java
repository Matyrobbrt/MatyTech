package com.matyrobbrt.matytech.api.util.helper;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class InventoryHelper {

	/**
	 * Creates slots for the player's inventory for a {@link Container}. Convenience
	 * method to improve readability of Container code.
	 *
	 * @param playerInventory Player's inventory
	 * @param startX          X-position of top-left slot
	 * @param startY          Y-position of top-left slot
	 * @return A collection of slots to be added
	 * @since 4.1.1
	 */
	public static Collection<Slot> createPlayerSlots(PlayerInventory playerInventory, int startX, int startY) {
		Collection<Slot> list = new ArrayList<>();
		// Backpack
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				list.add(new Slot(playerInventory, x + y * 9 + 9, startX + x * 18, startY + y * 18));
			}
		}
		// Hotbar
		for (int x = 0; x < 9; ++x) {
			list.add(new Slot(playerInventory, x, 8 + x * 18, startY + 58));
		}
		return list;
	}

}
