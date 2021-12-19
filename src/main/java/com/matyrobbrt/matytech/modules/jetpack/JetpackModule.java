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

package com.matyrobbrt.matytech.modules.jetpack;

import static com.matyrobbrt.matytech.api.util.helper.PlayerHelper.isOnGroundOrSleeping;

import java.util.function.BooleanSupplier;

import com.matyrobbrt.lib.annotation.RL;
import com.matyrobbrt.lib.module.IModule;
import com.matyrobbrt.lib.module.Module;
import com.matyrobbrt.lib.registry.annotation.RegisterItem;
import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.modules.jetpack.JetpackItem.JetpackMode;
import com.matyrobbrt.matytech.util.MTKeySync;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

@SuppressWarnings("static-method")
@Module(id = @RL(modid = MatyTech.MOD_ID, path = "jetpack"))
public class JetpackModule implements IModule {

	@RegisterItem("jetpack")
	public static final JetpackItem JETPACK_ITEM = new JetpackItem(EquipmentSlotType.CHEST, new Item.Properties());

	@Override
	public void register(IEventBus modBus, IEventBus forgeBus) {
		IModule.super.register(modBus, forgeBus);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> forgeBus.register(new JetpackClient()));
	}

	@SubscribeEvent
	public void playerTick(final PlayerTickEvent event) {
		if (event.phase == Phase.END && event.side.isServer()) {
			PlayerEntity player = event.player;
			ItemStack chestStack = player.inventory.armor.get(2);
			if (isJetpackOn(player, chestStack)) {
				JetpackMode jetpackMode = JetpackItem.getMode(chestStack);
				if (handleJetpackMotion(player, jetpackMode,
						() -> MatyTech.KEY_MAP.has(player.getUUID(), MTKeySync.ASCEND))) {
					player.fallDistance = 0.0F;
					if (player instanceof ServerPlayerEntity) {
						((ServerPlayerEntity) player).connection.aboveGroundTickCount = 0;
					}
				}
			}
		}
	}

	/**
	 * @return If fall distance should get reset or not
	 */
	public static boolean handleJetpackMotion(PlayerEntity player, JetpackMode mode,
			BooleanSupplier ascendingSupplier) {
		Vector3d motion = player.getDeltaMovement();
		if (mode == JetpackMode.NORMAL) {
			if (player.isFallFlying()) {
				Vector3d lookAngle = player.getLookAngle();
				Vector3d normalizedLook = lookAngle.normalize();
				double d1x = normalizedLook.x * 0.15;
				double d1y = normalizedLook.y * 0.15;
				double d1z = normalizedLook.z * 0.15;
				player.setDeltaMovement(motion.add(lookAngle.x * d1x + (lookAngle.x * 1.5 - motion.x) * 0.5,
						lookAngle.y * d1y + (lookAngle.y * 1.5 - motion.y) * 0.5,
						lookAngle.z * d1z + (lookAngle.z * 1.5 - motion.z) * 0.5));
				return false;
			} else {
				player.setDeltaMovement(motion.x(), Math.min(motion.y() + 0.15D, 0.5D), motion.z());
			}
		} else if (mode == JetpackMode.HOVER) {
			boolean ascending = ascendingSupplier.getAsBoolean();
			boolean descending = player.isDescending();
			if ((!ascending && !descending) || (ascending && descending)) {
				if (motion.y() > 0) {
					player.setDeltaMovement(motion.x(), Math.max(motion.y() - 0.15D, 0), motion.z());
				} else if (motion.y() < 0) {
					if (!isOnGroundOrSleeping(player)) {
						player.setDeltaMovement(motion.x(), Math.min(motion.y() + 0.15D, 0), motion.z());
					}
				}
			} else if (ascending) {
				player.setDeltaMovement(motion.x(), Math.min(motion.y() + 0.15D, 0.2D), motion.z());
			} else if (!isOnGroundOrSleeping(player)) {
				player.setDeltaMovement(motion.x(), Math.max(motion.y() - 0.15D, -0.2D), motion.z());
			}
		}
		return true;
	}

	private static boolean isJetpackOn(PlayerEntity player, ItemStack chest) {
		if (!chest.isEmpty() && !player.isSpectator()) {
			JetpackMode mode = JetpackItem.getMode(chest);
			if (mode == JetpackMode.NORMAL) {
				return MatyTech.KEY_MAP.has(player.getUUID(), MTKeySync.ASCEND);
			} else if (mode == JetpackMode.HOVER) {
				boolean ascending = MatyTech.KEY_MAP.has(player.getUUID(), MTKeySync.ASCEND);
				boolean descending = player.isDescending();
				if (!ascending || descending) { return !isOnGroundOrSleeping(player); }
				return true;
			}
		}
		return false;
	}
}
