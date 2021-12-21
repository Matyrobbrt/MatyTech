package com.matyrobbrt.matytech.modules.eseparator.client;

import com.matyrobbrt.lib.util.helper.TernaryHelper;
import com.matyrobbrt.matytech.api.client.screen.MachineContainerScreen;
import com.matyrobbrt.matytech.api.client.screen.element.EnergyGuiElement;
import com.matyrobbrt.matytech.api.client.screen.element.EnergyGuiElement.Type;
import com.matyrobbrt.matytech.api.client.screen.element.FluidStackGuiElement;
import com.matyrobbrt.matytech.api.client.screen.element.GuiElement;
import com.matyrobbrt.matytech.api.util.MatyTechRL;
import com.matyrobbrt.matytech.modules.eseparator.ESeparatorContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ESeparatorScreen extends MachineContainerScreen<ESeparatorContainer> {

	public static final ResourceLocation TEXTURE = new MatyTechRL("textures/gui/machine/electrolytic_separator.png");

	public ESeparatorScreen(ESeparatorContainer pMenu, PlayerInventory pPlayerInventory) {
		super(pMenu, pPlayerInventory, new StringTextComponent("Electrolytic Separator"), TEXTURE);
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 220;
		this.imageHeight = 201;

		this.inventoryLabelX = this.leftPos + 30;
		this.titleLabelX = this.leftPos + 30;
	}

	@Override
	protected void init() {
		super.init();
		addWidget(new FluidStackGuiElement(this.leftPos + 152, this.topPos + 5, 16, 75,
				() -> this.menu.tile.getFluidInTank(0), () -> menu.tile.getTankCapacity(0)));
		addWidget(new EnergyGuiElement(this.leftPos + 7, this.topPos + 4, 18, 77, () -> menu.tile.energyStorage,
				Type.HORIZONTAL));
	}

	@Override
	protected void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
		super.renderBg(pMatrixStack, pPartialTicks, pX, pY);

		RenderSystem.color4f(1, 1, 1, 1);
		bind(TEXTURE);

		for (int i = 0; i <= 77; ++i) {
			if (TernaryHelper.supplier(() -> {
				return this.menu.tile.energyStorage.getEnergyStored() != 0
						&& this.menu.tile.energyStorage.getMaxEnergyStored() != 0
								? this.menu.tile.energyStorage.getEnergyStored() * 77
										/ this.menu.tile.energyStorage.getMaxEnergyStored()
								: 0;
			}) >= i) {
				this.blit(pMatrixStack, this.leftPos + 7, this.topPos + 4 + 77 - i, 238, 0 + 77 - i, 18, 1);
			}
		}
	}

	@Override
	public void renderTooltip(MatrixStack pMatrixStack, ITextComponent pText, int pMouseX, int pMouseY) {
		super.renderTooltip(pMatrixStack, pText, pMouseX, pMouseY);
		children.stream().filter(GuiElement.class::isInstance).map(GuiElement.class::cast)
				.forEach(w -> w.renderToolTip(pMatrixStack, pMouseX, pMouseY));
	}
}
