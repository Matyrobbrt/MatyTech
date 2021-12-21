package com.matyrobbrt.matytech.api.util;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class Serializers {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	public static FluidStack deserializeFluid(@Nonnull JsonObject json) {
		if (!json.has("amount")) {
			throw new JsonSyntaxException("Expected to receive a amount that is greater than zero");
		}
		JsonElement count = json.get("amount");
		if (!JSONUtils.isNumberValue(count)) {
			throw new JsonSyntaxException("Expected amount to be a number greater than zero.");
		}
		int amount = count.getAsJsonPrimitive().getAsInt();
		if (amount < 1) { throw new JsonSyntaxException("Expected amount to be greater than zero."); }
		ResourceLocation resourceLocation = new ResourceLocation(JSONUtils.getAsString(json, "fluid"));
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(resourceLocation);
		if (fluid == null || fluid == Fluids.EMPTY) {
			throw new JsonSyntaxException("Invalid fluid type '" + resourceLocation + "'");
		}
		CompoundNBT nbt = null;
		if (json.has("nbt")) {
			JsonElement jsonNBT = json.get("nbt");
			try {
				if (jsonNBT.isJsonObject()) {
					nbt = JsonToNBT.parseTag(GSON.toJson(jsonNBT));
				} else {
					nbt = JsonToNBT.parseTag(JSONUtils.convertToString(jsonNBT, "nbt"));
				}
			} catch (CommandSyntaxException e) {
				throw new JsonSyntaxException("Invalid NBT entry for fluid '" + resourceLocation + "'");
			}
		}
		return new FluidStack(fluid, amount, nbt);
	}

}
