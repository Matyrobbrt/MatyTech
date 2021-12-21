package com.matyrobbrt.matytech.api.client.screen.element;

import java.util.List;
import java.util.function.Supplier;

import com.matyrobbrt.matytech.api.util.helper.GuiHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.fluids.FluidStack;

public class FluidStackGuiElement extends GuiElement {

	protected final Supplier<FluidStack> fluidStack;
	protected final Supplier<Integer> capacity;

	public FluidStackGuiElement(int pX, int pY, int pWidth, int pHeight, Supplier<FluidStack> fluidStack,
			Supplier<Integer> capacity) {
		super(pX, pY, pWidth, pHeight, new StringTextComponent("Fluid"));
		this.fluidStack = fluidStack;
		this.capacity = capacity;
	}

	@Override
	public void renderHoverToolTip(MatrixStack pMatrixStack, List<ITextProperties> tooltips, int pMouseX, int pMouseY,
			FontRenderer font) {
		super.renderHoverToolTip(pMatrixStack, tooltips, pMouseX, pMouseY, font);
		tooltips.add(fluidStack.get().getDisplayName());
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		GuiHelper.drawFluid(pMatrixStack, x, y, width, height, capacity.get(), fluidStack.get());
		RenderSystem.disableBlend();
		RenderSystem.disableAlphaTest();
	}

}
