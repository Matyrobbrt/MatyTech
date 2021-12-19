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

import com.matyrobbrt.matytech.api.util.Pos3D;
import com.matyrobbrt.matytech.api.util.helper.PlayerHelper;
import com.matyrobbrt.matytech.client.MTClientSetup;
import com.matyrobbrt.matytech.modules.jetpack.JetpackItem.JetpackMode;
import com.matyrobbrt.matytech.util.MTKeySync;
import com.matyrobbrt.matytech.util.PlayerGearData;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("static-method")
public class JetpackClient {

	private static final Minecraft minecraft = Minecraft.getInstance();

	@SubscribeEvent
	public void clientTick(final ClientTickEvent event) {
		if (event.phase == Phase.START
				&& (minecraft.level != null && minecraft.player != null && !minecraft.isPaused())) {
			PlayerGearData.INSTANCE.setJetpackState(minecraft.player.getUUID(), isJetpackActive(minecraft.player),
					true);

			ItemStack chestStack = minecraft.player.inventory.armor.get(2);
			if (!chestStack.isEmpty() && (chestStack.getItem() instanceof JetpackItem)) {
				MTClientSetup.updateKey(minecraft.player.input.jumping, MTKeySync.ASCEND);
			}

			if (isJetpackActive(minecraft.player)) {
				if (JetpackModule.handleJetpackMotion(minecraft.player, JetpackItem.getMode(chestStack),
						() -> minecraft.player.input.jumping)) {
					minecraft.player.fallDistance = 0.0F;
				}
			}
		}
	}

	@SubscribeEvent
	public void renderTick(final RenderTickEvent event) {
		if (event.phase != Phase.END) { return; }
		if (minecraft.level == null) { return; }

		World level = minecraft.level;
		PlayerEntity clientPlayer = minecraft.player;

		PlayerGearData.INSTANCE.getActiveJetpacks().forEach(uuid -> {
			PlayerEntity player = level.getPlayerByUUID(uuid);
			if (player != null) {
				Pos3D playerPos = new Pos3D(player).translate(0, player.getEyeHeight(), 0);
				Vector3d motion = player.getDeltaMovement();
				float random = (level.random.nextFloat() - 0.5F) * 0.1F;
				float xRot;
				if (player.isCrouching()) {
					xRot = 20;
					playerPos = playerPos.translate(0, 0.125, 0);
				} else {
					float f = player.getSwimAmount(event.renderTickTime);
					if (player.isFallFlying()) {
						float f1 = player.getFallFlyingTicks() + event.renderTickTime;
						float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
						xRot = f2 * (-90.0F - player.xRot);
					} else {
						float f3 = player.isInWater() ? -90.0F - player.xRot : -90.0F;
						xRot = MathHelper.lerp(f, 0.0F, f3);
					}
					xRot = -xRot;
					Pos3D eye = new Pos3D(0, player.getEyeHeight(), 0).xRot(xRot).yRot(player.yBodyRot);
					if (player.isFallFlying()
							&& (player != clientPlayer || !minecraft.options.getCameraType().isFirstPerson())) {
						eye = new Pos3D(0, player.getEyeHeight(Pose.STANDING), 0).xRot(xRot).yRot(player.yBodyRot);
					} else if (player.isVisuallySwimming()) {
						eye = new Pos3D(0, player.getEyeHeight(), 0).xRot(xRot).yRot(player.yBodyRot).translate(0, 0.5,
								0);
					}
					playerPos = new Pos3D(player.getX() + eye.x, player.getY() + eye.y, player.getZ() + eye.z);
				}
				Pos3D vLeft = new Pos3D(-0.35, -.75, -0.50).xRot(xRot).yRot(player.yBodyRot);
				renderJetpackFlame(level, playerPos.translate(vLeft, motion),
						vLeft.scale(0.2).translate(motion, vLeft.scale(random)));
				Pos3D vRight = new Pos3D(0.35, -.75, -0.50).xRot(xRot).yRot(player.yBodyRot);
				renderJetpackFlame(level, playerPos.translate(vRight, motion),
						vRight.scale(0.2).translate(motion, vRight.scale(random)));
			}
		});
	}

	public static boolean isJetpackActive(PlayerEntity player) {
		if (player != minecraft.player) { return PlayerGearData.INSTANCE.isJetpackOn(player); }
		if (!player.isSpectator()) {
			ItemStack chest = player.getItemBySlot(EquipmentSlotType.CHEST);
			if (!chest.isEmpty()) {
				JetpackMode mode = JetpackItem.getMode(chest);
				if (mode == JetpackMode.NORMAL) {
					return minecraft.screen == null && minecraft.player.input.jumping;
				} else if (mode == JetpackMode.HOVER) {
					boolean ascending = minecraft.player.input.jumping;
					boolean descending = minecraft.player.input.shiftKeyDown;
					if (!ascending || descending || minecraft.screen != null) {
						return !PlayerHelper.isOnGroundOrSleeping(player);
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This method adds the particles 2 times
	 * 
	 * @param level
	 * @param pos
	 * @param motion
	 */
	private void renderJetpackFlame(World level, Vector3d pos, Vector3d motion) {
		// level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, motion.x,
		// motion.y, motion.z);
		level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
	}

}
