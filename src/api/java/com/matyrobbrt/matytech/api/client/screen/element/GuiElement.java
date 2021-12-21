package com.matyrobbrt.matytech.api.client.screen.element;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public abstract class GuiElement extends Widget {

	protected GuiElement(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
	}

	public void renderHoverToolTip(MatrixStack matrixStack, List<ITextProperties> tooltips, int mouseX, int mouseY,
			FontRenderer font) {
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
	}

	public void defaultRender(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}
