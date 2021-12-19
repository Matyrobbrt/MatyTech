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

package com.matyrobbrt.matytech.client.armour;

import com.matyrobbrt.lib.util.Utils;
import com.matyrobbrt.matytech.api.client.model.CustomArmourModel;
import com.matyrobbrt.matytech.api.item.CapabilityRenderableArmour;
import com.matyrobbrt.matytech.api.item.IRenderableArmour;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MTArmourLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>>
		extends BipedArmorLayer<T, M, A> {

	public MTArmourLayer(IEntityRenderer<T, M> entityRenderer, A modelLeggings, A modelArmor) {
		super(entityRenderer, modelLeggings, modelArmor);
	}

	@Override
	public void render(MatrixStack matrix, IRenderTypeBuffer renderer, int packedLight, T entity, float limbSwing,
			float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		renderPart(matrix, renderer, entity, EquipmentSlotType.CHEST, packedLight, partialTicks);
		renderPart(matrix, renderer, entity, EquipmentSlotType.LEGS, packedLight, partialTicks);
		renderPart(matrix, renderer, entity, EquipmentSlotType.FEET, packedLight, partialTicks);
		renderPart(matrix, renderer, entity, EquipmentSlotType.HEAD, packedLight, partialTicks);
	}

	@SuppressWarnings("unchecked")
	private void renderPart(MatrixStack matrix, IRenderTypeBuffer renderer, T entity, EquipmentSlotType slot, int light,
			float partialTicks) {
		ItemStack stack = entity.getItemBySlot(slot);
		Item item = stack.getItem();
		Utils.instanceOf(item, ArmorItem.class).ifPresent(armourItem -> {
			if (armourItem.getSlot() == slot) {
				Utils.instanceOf(item, IRenderableArmour.class).ifPresent(renderable -> {

					CustomArmourModel model = renderable.getModel(stack);
					getParentModel().copyPropertiesTo((BipedModel<T>) model);
					setPartVisibility((A) model, slot);
					model.render(matrix, renderer, light, OverlayTexture.NO_OVERLAY, partialTicks, stack.hasFoil(),
							entity, stack);
				});

				stack.getCapability(CapabilityRenderableArmour.RENDERABLE_ARMOUR_CAPABILITY).ifPresent(cap -> {
					CustomArmourModel model = cap.getModel(stack);
					getParentModel().copyPropertiesTo((BipedModel<T>) model);
					setPartVisibility((A) model, slot);
					model.render(matrix, renderer, light, OverlayTexture.NO_OVERLAY, partialTicks, stack.hasFoil(),
							entity, stack);
				});
			}
		});
	}

}
