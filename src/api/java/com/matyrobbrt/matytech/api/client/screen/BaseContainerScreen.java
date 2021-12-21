package com.matyrobbrt.matytech.api.client.screen;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.matyrobbrt.matytech.api.client.screen.element.GuiElement;
import com.matyrobbrt.matytech.api.util.NumberUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public class BaseContainerScreen<C extends Container> extends ContainerScreen<C> {

	protected final ResourceLocation bgTexture;

	public BaseContainerScreen(C pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle,
			ResourceLocation bgTexture) {
		super(pMenu, pPlayerInventory, pTitle);
		this.bgTexture = bgTexture;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);

		List<ITextProperties> tooltips = Lists.newArrayList();
		getGuiElements().forEach(elem -> {
			if (NumberUtils.isMouseBetween(mouseX, mouseY, elem.x, elem.y, elem.getWidth(), elem.getHeight())) {
				elem.renderHoverToolTip(matrixStack, tooltips, mouseX, mouseY, font);
			}
		});
		renderWrappedToolTip(matrixStack, tooltips, mouseX, mouseY, font);
	}

	@Override
	protected void renderTooltip(MatrixStack pPoseStack, int pX, int pY) {
		super.renderTooltip(pPoseStack, pX, pY);
	}

	@Override
	protected void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
		RenderSystem.color4f(1f, 1f, 1f, 1f);
		minecraft.textureManager.bind(this.bgTexture);
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		this.blit(pMatrixStack, x, y, 0, 0, imageWidth, imageHeight);

		getGuiElements().forEach(e -> e.render(pMatrixStack, x, y, pPartialTicks));
	}

	@Override
	public void renderToolTip(MatrixStack pMatrixStack, List<? extends IReorderingProcessor> pTooltips, int pMouseX,
			int pMouseY, FontRenderer font) {
		super.renderToolTip(pMatrixStack, pTooltips, pMouseX, pMouseY, font);
		List<ITextProperties> tooltips = Lists.newArrayList();
		System.out.println("e");
		getGuiElements().forEach(elem -> {
			if (NumberUtils.isMouseBetween(pMouseX, pMouseY, elem.x, elem.y, elem.getWidth(), elem.getHeight())) {
				System.out.println("e");
				elem.renderHoverToolTip(pMatrixStack, tooltips, pMouseX, pMouseY, font);
			}
		});
		renderWrappedToolTip(pMatrixStack, tooltips, pMouseX, pMouseY, font);
	}

	protected void bind(ResourceLocation texture) {
		minecraft.getTextureManager().bind(texture);
	}

	public Stream<GuiElement> getGuiElements() {
		return children.stream().filter(GuiElement.class::isInstance).map(GuiElement.class::cast);
	}

}
