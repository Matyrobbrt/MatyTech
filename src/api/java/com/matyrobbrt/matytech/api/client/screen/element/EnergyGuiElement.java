package com.matyrobbrt.matytech.api.client.screen.element;

import java.util.List;
import java.util.function.Supplier;

import com.matyrobbrt.lib.util.ColourCodes;
import com.matyrobbrt.matytech.api.capability.MTEnergyStorage;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class EnergyGuiElement extends GuiElement {

	private final Supplier<MTEnergyStorage> energyStorage;
	private final Type type;

	public EnergyGuiElement(int pX, int pY, int pWidth, int pHeight, Supplier<MTEnergyStorage> energyStorage,
			Type type) {
		super(pX, pY, pWidth, pHeight, new StringTextComponent("Energy"));
		this.energyStorage = energyStorage;
		this.type = type;
	}

	@Override
	public void renderHoverToolTip(MatrixStack matrixStack, List<ITextProperties> tooltips, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderHoverToolTip(matrixStack, tooltips, mouseX, mouseY, font);
		tooltips.add(
				new StringTextComponent("Energy Stored: " + ColourCodes.GOLD + energyStorage.get().getEnergyStored()));
	}

	public static enum Type {
		HORIZONTAL, VERTICAL
	}

}
