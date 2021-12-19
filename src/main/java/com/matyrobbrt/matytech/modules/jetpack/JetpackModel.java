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

import com.matyrobbrt.matytech.api.client.model.MTJavaModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports

public class JetpackModel extends MTJavaModel {

	private final ModelRenderer jetpack;
	private final ModelRenderer left_curved_r1;
	private final ModelRenderer right_curved_r1;
	private final ModelRenderer right_propeller_r1;

	private final RenderType renderType;

	public JetpackModel(ResourceLocation texture) {
		super(RenderType::entitySolid);
		this.renderType = renderType(texture);
		texWidth = 32;
		texHeight = 32;

		jetpack = new ModelRenderer(this);
		jetpack.setPos(0.0F, 24.0F, 0.0F);
		jetpack.texOffs(20, 0).addBox(-2.0F, -21.0F, 2.0F, 4.0F, 4.0F, 2.0F, 0.0F, false);
		jetpack.texOffs(26, 26).addBox(-3.0F, -24.0F, 2.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
		jetpack.texOffs(0, 27).addBox(-3.0F, -24.0F, -3.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		jetpack.texOffs(26, 19).addBox(2.0F, -24.0F, 2.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
		jetpack.texOffs(4, 26).addBox(2.0F, -24.0F, -3.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		jetpack.texOffs(5, 27).addBox(-3.0F, -24.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		jetpack.texOffs(16, 27).addBox(2.0F, -24.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		jetpack.texOffs(0, 17).addBox(-3.0F, -20.0F, -3.0F, 6.0F, 7.0F, 1.0F, 0.0F, false);
		jetpack.texOffs(0, 6).addBox(-7.0F, -17.0F, 2.0F, 14.0F, 1.0F, 2.0F, 0.0F, false);

		left_curved_r1 = new ModelRenderer(this);
		left_curved_r1.setPos(-2.6962F, -18.5359F, 3.0F);
		jetpack.addChild(left_curved_r1);
		setRotationAngle(left_curved_r1, 0.0F, 0.0F, -0.6545F);
		left_curved_r1.texOffs(0, 10).addBox(-4.4F, -1.7F, -1.0F, 6.0F, 4.0F, 2.0F, 0.0F, false);

		right_curved_r1 = new ModelRenderer(this);
		right_curved_r1.setPos(-2.6962F, -18.5359F, 3.0F);
		jetpack.addChild(right_curved_r1);
		setRotationAngle(right_curved_r1, 0.0F, 0.0F, 0.6545F);
		right_curved_r1.texOffs(0, 0).addBox(2.7F, -5.0F, -1.0F, 6.0F, 4.0F, 2.0F, 0.0F, false);

		right_propeller_r1 = new ModelRenderer(this);
		right_propeller_r1.setPos(-6.0F, -15.5F, 3.5F);
		jetpack.addChild(right_propeller_r1);
		setRotationAngle(right_propeller_r1, -0.9163F, 0.0F, 0.0F);
		right_propeller_r1.texOffs(14, 12).addBox(9.0F, -0.5F, -1.5F, 4.0F, 2.0F, 5.0F, 0.0F, false);
		right_propeller_r1.texOffs(10, 20).addBox(-1.0F, -0.5F, -1.5F, 4.0F, 2.0F, 5.0F, 0.0F, false);
	}

	public void render(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight,
			boolean hasEffect) {
		renderToBuffer(matrix, getVertexBuilder(renderer, renderType, hasEffect), light, overlayLight, 1, 1, 1, 1);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		jetpack.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public static void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
