/**
 * This file is part of the MatyTech Minecraft (Java Edition) mod and is
 * licensed under the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2021 Matyrobbrt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.matyrobbrt.matytech.util;

import java.util.Set;
import java.util.UUID;

import com.matyrobbrt.matytech.network.MTNetwork;
import com.matyrobbrt.matytech.network.message.UpdateGearMessage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class PlayerGearData {

	public static final PlayerGearData INSTANCE = new PlayerGearData();

	private final Set<UUID> activeJetpacks = new ObjectOpenHashSet<>();

	private IWorld level;

	public void clear(boolean isClientSide) {
		activeJetpacks.clear();
	}

	public void clearPlayer(UUID uuid, boolean isClientSide) {
		activeJetpacks.remove(uuid);
	}

	public void initializeLevel(IWorld level) {
		this.level = level;
	}

	/**
	 * ===============
	 * 
	 * JETPACK
	 * 
	 * ================
	 */

	public void setJetpackState(UUID uuid, boolean isActive, boolean isClientSide) {
		boolean alreadyActive = activeJetpacks.contains(uuid);
		boolean changed = alreadyActive != isActive;
		if (alreadyActive && !isActive) {
			activeJetpacks.remove(uuid);
		} else if (!alreadyActive && isActive) {
			activeJetpacks.add(uuid);
		}

		if (changed && level.isClientSide()) {
			if (isClientSide) {
				MTNetwork.MAIN_CHANNEL.sendToServer(new UpdateGearMessage(
						com.matyrobbrt.matytech.network.message.UpdateGearMessage.GearType.JETPACK, uuid, isActive));
			}
		}
	}

	public boolean isJetpackOn(PlayerEntity p) {
		return activeJetpacks.contains(p.getUUID());
	}

	public Set<UUID> getActiveJetpacks() { return activeJetpacks; }

}
