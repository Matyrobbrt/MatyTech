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

import com.matyrobbrt.lib.network.INetworkMessage;
import com.matyrobbrt.matytech.MatyTech;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent;

public class KeyMessage implements INetworkMessage {

	private final int key;
	private final boolean add;

	public KeyMessage(int key, boolean add) {
		this.key = key;
		this.add = add;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		PlayerEntity player = context.getSender();
		if (player != null) {
			if (add) {
				MatyTech.KEY_MAP.add(player.getUUID(), key);
			} else {
				MatyTech.KEY_MAP.remove(player.getUUID(), key);
			}
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeVarInt(key);
		buffer.writeBoolean(add);
	}

	public static KeyMessage decode(PacketBuffer buffer) {
		return new KeyMessage(buffer.readVarInt(), buffer.readBoolean());
	}

}
