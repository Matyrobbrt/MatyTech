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

import java.util.Map;
import java.util.UUID;

import com.matyrobbrt.matytech.modules.jetpack.JetpackItem.JetpackMode;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/**
 * Used to simulate key presses. Mainly for the {@link JetpackMode}
 */
public class MTKeySync {

	public static final int ASCEND = 0;
	public static final int BOOST = 1;

	public final Map<UUID, KeySet> keys = new Object2ObjectOpenHashMap<>();

	public KeySet getPlayerKeys(UUID playerUUID) {
		return keys.get(playerUUID);
	}

	public void add(UUID playerUUID, int key) {
		if (keys.containsKey(playerUUID)) {
			keys.get(playerUUID).keysActive.add(key);
		} else {
			keys.put(playerUUID, new KeySet(key));
		}
	}

	public void remove(UUID playerUUID, int key) {
		if (keys.containsKey(playerUUID)) {
			keys.get(playerUUID).keysActive.remove(key);
		}
	}

	public boolean has(UUID playerUUID, int key) {
		return keys.containsKey(playerUUID) && keys.get(playerUUID).keysActive.contains(key);
	}

	public void update(UUID playerUUID, int key, boolean add) {
		if (add) {
			add(playerUUID, key);
		} else {
			remove(playerUUID, key);
		}
	}

	public static class KeySet {

		public final IntSet keysActive = new IntOpenHashSet();

		public KeySet(int key) {
			keysActive.add(key);
		}
	}

}
