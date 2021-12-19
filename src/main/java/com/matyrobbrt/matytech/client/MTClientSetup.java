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

package com.matyrobbrt.matytech.client;

import java.util.Map.Entry;
import java.util.UUID;

import com.matyrobbrt.lib.ClientSetup;
import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.client.armour.MTArmourLayer;
import com.matyrobbrt.matytech.network.MTNetwork;
import com.matyrobbrt.matytech.network.message.KeyMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class MTClientSetup extends ClientSetup {

	/**
	 * Does not follow naming convertions for readability
	 */
	private static final Minecraft minecraft = Minecraft.getInstance();

	public MTClientSetup(IEventBus modBus) {
		super(modBus);
		modBus.addListener(this::loadComplete);
		modBus.register(this);
	}

	@SuppressWarnings("unchecked")
	public void loadComplete(final FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			EntityRendererManager entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
			// Add Player layers
			for (Entry<String, PlayerRenderer> entry : entityRenderManager.getSkinMap().entrySet()) {
				addCustomLayers(EntityType.PLAYER, entry.getValue());
			}

			// Add mob layers
			for (Entry<EntityType<?>, EntityRenderer<?>> entry : entityRenderManager.renderers.entrySet()) {
				EntityRenderer<?> renderer = entry.getValue();
				if (renderer instanceof LivingRenderer) {
					addCustomLayers(entry.getKey(), (LivingRenderer<LivingEntity, BipedModel<LivingEntity>>) renderer);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends LivingEntity, M extends BipedModel<T>> void addCustomLayers(EntityType<?> type,
			LivingRenderer<T, M> renderer) {
		BipedArmorLayer<T, M, ?> bipedArmorLayer = null;
		for (LayerRenderer<T, M> layerRenderer : renderer.layers) {
			if (layerRenderer != null) {
				// Only on exact class match
				Class<?> layerClass = layerRenderer.getClass();
				if (layerClass == BipedArmorLayer.class) {
					bipedArmorLayer = (BipedArmorLayer<T, M, ?>) layerRenderer;
				}
			}
		}
		if (bipedArmorLayer != null) {
			renderer.addLayer(new MTArmourLayer<>(renderer, bipedArmorLayer.innerModel, bipedArmorLayer.outerModel));
			MatyTech.LOGGER.debug("Added MT Armour Layer to entity of type: {}", type.getRegistryName());
		}
	}

	public static void updateKey(KeyBinding key, int type) {
		updateKey(key.isDown(), type);
	}

	public static void updateKey(boolean pressed, int type) {
		if (minecraft.player != null) {
			UUID playerUUID = minecraft.player.getUUID();
			boolean down = minecraft.screen == null && pressed;
			if (down != MatyTech.KEY_MAP.has(playerUUID, type)) {
				MTNetwork.MAIN_CHANNEL.sendToServer(new KeyMessage(type, down));
				MatyTech.KEY_MAP.update(playerUUID, type, down);
			}
		}
	}

}
