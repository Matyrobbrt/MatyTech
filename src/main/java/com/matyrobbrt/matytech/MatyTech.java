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

package com.matyrobbrt.matytech;

import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.matyrobbrt.lib.ClientSetup;
import com.matyrobbrt.lib.ModSetup;
import com.matyrobbrt.lib.registry.annotation.AnnotationProcessor;
import com.matyrobbrt.matytech.api.item.CapabilityRenderableArmour;
import com.matyrobbrt.matytech.client.MTClientSetup;
import com.matyrobbrt.matytech.network.MTNetwork;
import com.matyrobbrt.matytech.util.MTKeySync;
import com.matyrobbrt.matytech.util.PlayerGearData;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("static-method")
@Mod(MatyTech.MOD_ID)
public class MatyTech extends ModSetup {

	public static final String MOD_ID = "matytech";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final MTKeySync KEY_MAP = new MTKeySync();

	public static final AnnotationProcessor ANN_PROCESSOR = new AnnotationProcessor(MOD_ID);

	public MatyTech() {
		super(MOD_ID);
	}

	@Override
	public AnnotationProcessor annotationProcessor() {
		return ANN_PROCESSOR;
	}

	@Override
	public Optional<Supplier<ClientSetup>> clientSetup() {
		return Optional.of(() -> new MTClientSetup(modBus));
	}

	@Override
	public void onCommonSetup(FMLCommonSetupEvent event) {
		CapabilityRenderableArmour.register();
		MTNetwork.register();
	}

	@SubscribeEvent
	public void onWorldLoad(final WorldEvent.Load event) {
		PlayerGearData.INSTANCE.initializeLevel(event.getWorld());
	}

	public static final ItemGroup MATY_TECH_TAB = new ItemGroup(ItemGroup.TABS.length, "matytech") {

		@Override
		public ItemStack makeIcon() {
			return ItemStack.EMPTY;
		}
	};
}
