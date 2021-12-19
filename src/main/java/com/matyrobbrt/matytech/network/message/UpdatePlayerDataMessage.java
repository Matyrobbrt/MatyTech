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

package com.matyrobbrt.matytech.network.message;

import java.util.UUID;

import com.matyrobbrt.lib.network.INetworkMessage;
import com.matyrobbrt.matytech.util.PlayerGearData;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent;

public class UpdatePlayerDataMessage implements INetworkMessage {

	private final UUID uuid;
	private final boolean activeJetpack;

	public UpdatePlayerDataMessage(UUID uuid) {
		this.uuid = uuid;
		this.activeJetpack = PlayerGearData.INSTANCE.getActiveJetpacks().contains(uuid);
	}

	private UpdatePlayerDataMessage(UUID uuid, boolean activeJetpack) {
		this.uuid = uuid;
		this.activeJetpack = activeJetpack;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		PlayerGearData.INSTANCE.setJetpackState(uuid, activeJetpack, false);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeUUID(uuid);
		buffer.writeBoolean(activeJetpack);
	}

	public static UpdatePlayerDataMessage decode(PacketBuffer buffer) {
		return new UpdatePlayerDataMessage(buffer.readUUID(), buffer.readBoolean());
	}

}
