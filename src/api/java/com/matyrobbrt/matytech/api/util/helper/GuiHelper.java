package com.matyrobbrt.matytech.api.util.helper;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class GuiHelper {

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		Minecraft minecraft = Minecraft.getInstance();
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes attributes = fluid.getAttributes();
		ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
		return minecraft.getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluidStill);
	}

	/**
	 * The fluid starts rendering at the top left corner, going to the right in
	 * width, and down in height
	 * 
	 * @param matrixStack
	 * @param xPosition
	 * @param yPosition
	 * @param width
	 * @param height
	 * @param capacity
	 * @param fluidStack
	 */
	public static void drawFluid(MatrixStack matrixStack, final int xPosition, final int yPosition, int width,
			int height, int capacity, @Nullable FluidStack fluidStack) {
		if (fluidStack == null) { return; }
		if (fluidStack.isEmpty()) { return; }
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) { return; }

		TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

		FluidAttributes attributes = fluid.getAttributes();
		int fluidColor = attributes.getColor(fluidStack);

		int amount = fluidStack.getAmount();
		int scaledAmount = (amount * height) / capacity;
		if (amount > 0 && scaledAmount < 1) {
			scaledAmount = 1;
		}
		if (scaledAmount > height) {
			scaledAmount = height;
		}

		drawTiledSprite(matrixStack, xPosition, yPosition, fluidColor, fluidStillSprite, width, height, scaledAmount);
	}

	public static void drawTiledSprite(MatrixStack matrixStack, final int xPosition, final int yPosition, int color,
			TextureAtlasSprite sprite, int tiledWidth, int tiledHeight, int scaledAmount) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
		Matrix4f matrix = matrixStack.last().pose();
		setGLColorFromInt(color);

		final int yStart = yPosition + tiledHeight;
		final int xTileCount = tiledWidth / 16;
		final int xRemainder = tiledWidth - (xTileCount * 16);
		final int yTileCount = scaledAmount / 16;
		final int yRemainder = scaledAmount - (yTileCount * 16);

		for (int xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : 16;
				int height = (yTile == yTileCount) ? yRemainder : 16;
				int x = xPosition + (xTile * 16);
				int y = yStart - ((yTile + 1) * 16);
				if (width > 0 && height > 0) {
					int maskTop = 16 - height;
					int maskRight = 16 - width;

					drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
				}
			}
		}
	}

	public static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		float alpha = ((color >> 24) & 0xFF) / 255F;

		RenderSystem.color4f(red, green, blue, alpha);
	}

	public static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord,
			TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
		float uMin = textureSprite.getU0();
		float uMax = textureSprite.getU1();
		float vMin = textureSprite.getV0();
		float vMax = textureSprite.getV1();
		uMax = uMax - (maskRight / 16F * (uMax - uMin));
		vMax = vMax - (maskTop / 16F * (vMax - vMin));

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
		bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
		bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
		bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
		tessellator.end();
	}

}
