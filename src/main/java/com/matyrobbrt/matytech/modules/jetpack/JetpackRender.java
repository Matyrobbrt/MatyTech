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

import javax.annotation.Nonnull;

import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.api.client.model.CustomArmourModel;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class JetpackRender extends CustomArmourModel {

	public static final JetpackRender RENDERER = new JetpackRender(0.5f);
	private static final JetpackModel MODEL = new JetpackModel(
			new ResourceLocation(MatyTech.MOD_ID, "render/jetpack.png"));

	private JetpackRender(float size) {
		super(size);
	}

	@Override
	public void render(MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight, float partialTicks,
			boolean hasEffect, LivingEntity entity, ItemStack stack) {
		if (!body.visible) { return; }
		render(matrix, renderer, light, overlayLight, hasEffect);
	}

	private void render(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight,
			boolean hasEffect) {
		matrix.pushPose();
		body.translateAndRotate(matrix);
		matrix.translate(0, 0, 0.0);
		MODEL.render(matrix, renderer, light, overlayLight, hasEffect);
		matrix.popPose();
	}

}
