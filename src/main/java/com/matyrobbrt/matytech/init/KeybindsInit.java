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

package com.matyrobbrt.matytech.init;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import com.matyrobbrt.lib.keybind.BaseKeyBinding;
import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.network.MTNetwork;
import com.matyrobbrt.matytech.network.message.CycleItemModeMessage;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MatyTech.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class KeybindsInit {

	public static final BaseKeyBinding CYCLE_CHESTPLATE_MODE = create("cycle_chestplate_mode", KeyEvent.VK_C, mc -> {
		System.out.println("ye");
		MTNetwork.MAIN_CHANNEL.sendToServer(new CycleItemModeMessage(true, 2));
	}, false);

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(CYCLE_CHESTPLATE_MODE);
	}

	private static BaseKeyBinding create(String name, int key, Consumer<Minecraft> toRun, boolean checkEveryTick) {
		return new BaseKeyBinding("key." + MatyTech.MOD_ID + "." + name, key, "key.category." + MatyTech.MOD_ID, toRun,
				checkEveryTick);
	}
}
