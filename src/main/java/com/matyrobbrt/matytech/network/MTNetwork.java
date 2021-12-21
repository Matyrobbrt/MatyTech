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

package com.matyrobbrt.matytech.network;

import com.matyrobbrt.lib.network.BaseNetwork;
import com.matyrobbrt.lib.network.matylib.SyncValuesMessage;
import com.matyrobbrt.matytech.api.util.MatyTechRL;
import com.matyrobbrt.matytech.network.message.CycleItemModeMessage;
import com.matyrobbrt.matytech.network.message.KeyMessage;
import com.matyrobbrt.matytech.network.message.UpdateGearMessage;
import com.matyrobbrt.matytech.network.message.UpdatePlayerDataMessage;

import net.minecraft.entity.Entity;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MTNetwork extends BaseNetwork {

	private static final String NETWORK_VERSION = "0.2.0";

	public static final SimpleChannel MAIN_CHANNEL = newSimpleChannel("main");

	public static final SimpleChannel TILE_SYNCING = newSimpleChannel("tile_syncing");

	public static void register() {
		registerClientToServer(MAIN_CHANNEL, KeyMessage.class, KeyMessage::decode);
		registerClientToServer(MAIN_CHANNEL, UpdateGearMessage.class, UpdateGearMessage::decode);

		// Could be send both ways
		registerClientToServer(MAIN_CHANNEL, UpdatePlayerDataMessage.class, UpdatePlayerDataMessage::decode);
		registerServerToClient(MAIN_CHANNEL, UpdatePlayerDataMessage.class, UpdatePlayerDataMessage::decode);

		registerClientToServer(MAIN_CHANNEL, CycleItemModeMessage.class, CycleItemModeMessage::decode);

		/**
		 * No client -> server yet! That does seem to break things
		 */
		registerServerToClient(TILE_SYNCING, SyncValuesMessage.class, SyncValuesMessage::decode);
	}

	private static SimpleChannel newSimpleChannel(String name) {
		return NetworkRegistry.newSimpleChannel(new MatyTechRL(name), () -> NETWORK_VERSION,
				version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));
	}

	public static <M> void sendToAllTracking(SimpleChannel channel, M message, Entity entity) {
		channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
	}
}
