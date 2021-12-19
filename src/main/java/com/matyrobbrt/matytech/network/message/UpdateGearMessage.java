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
import com.matyrobbrt.matytech.network.MTNetwork;
import com.matyrobbrt.matytech.util.PlayerGearData;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateGearMessage implements INetworkMessage {

	private final GearType gearType;
	private final boolean state;
	private final UUID uuid;

	public UpdateGearMessage(GearType gearType, UUID uuid, boolean state) {
		this.gearType = gearType;
		this.uuid = uuid;
		this.state = state;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		if (gearType == GearType.JETPACK) {
			PlayerGearData.INSTANCE.setJetpackState(uuid, state, false);
		}
		PlayerEntity player = context.getSender();
		if (player != null) {
			MTNetwork.sendToAllTracking(MTNetwork.MAIN_CHANNEL, new UpdatePlayerDataMessage(uuid), player);
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeEnum(gearType);
		buffer.writeUUID(uuid);
		buffer.writeBoolean(state);
	}

	public static UpdateGearMessage decode(PacketBuffer buffer) {
		return new UpdateGearMessage(buffer.readEnum(GearType.class), buffer.readUUID(), buffer.readBoolean());
	}

	public enum GearType { JETPACK }

}
