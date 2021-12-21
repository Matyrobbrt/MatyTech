package com.matyrobbrt.matytech.modules.eseparator;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.lib.registry.annotation.recipe.RegisterRecipeSerializer;
import com.matyrobbrt.lib.registry.annotation.recipe.RegisterRecipeType;
import com.matyrobbrt.lib.util.helper.TernaryHelper;
import com.matyrobbrt.matytech.api.ingredient.FluidStackIngredient;
import com.matyrobbrt.matytech.api.tile.IFluidInventory;
import com.matyrobbrt.matytech.api.util.JsonConstants;
import com.matyrobbrt.matytech.api.util.Serializers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistryEntry;

@RegistryHolder(modid = com.matyrobbrt.matytech.api.util.ModIDs.MATY_TECH)
public class ESeparatorRecipe implements IRecipe<IFluidInventory> {

	@RegisterRecipeSerializer("electrolytic_separating")
	public static final Serializer SERIALIZER = new Serializer();

	@RegisterRecipeType("electrolytic_separating")
	public static final IRecipeType<ESeparatorRecipe> RECIPE_TYPE = new IRecipeType<ESeparatorRecipe>() {};

	private final ResourceLocation id;
	private final int processTime;
	private final FluidStackIngredient input;
	private final FluidStack output1;
	private final FluidStack output2;

	public ESeparatorRecipe(ResourceLocation id, int processTime, FluidStackIngredient input, FluidStack output1,
			FluidStack output2) {
		this.id = id;
		this.processTime = processTime;
		this.input = input;
		this.output1 = output1;
		this.output2 = output2;
	}

	@Override
	public boolean matches(IFluidInventory pInv, World pLevel) {
		boolean isValid = input.test(pInv.getFluidInTank(0));
		if (pInv.fillInternally(1, output1, FluidAction.SIMULATE) < output1.getAmount() && !output1.isEmpty()) {
			isValid = false;
		}
		if (pInv.fillInternally(2, output2, FluidAction.SIMULATE) < output2.getAmount() && !output2.isEmpty()) {
			isValid = false;
		}
		return isValid;
	}

	@Override
	public ItemStack assemble(IFluidInventory pInv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	public FluidStack getOutput1() { return output1; }

	public FluidStack getOutput2() { return output2; }

	public int getProcessTime() { return processTime; }

	public FluidStackIngredient getInputFluid() { return input; }

	@Override
	public ItemStack getResultItem() { return ItemStack.EMPTY; }

	@Override
	public ResourceLocation getId() { return id; }

	@Override
	public IRecipeSerializer<?> getSerializer() { return SERIALIZER; }

	@Override
	public IRecipeType<?> getType() { return RECIPE_TYPE; }

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<ESeparatorRecipe> {

		@Override
		public ESeparatorRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {

			int processTime = json.has(JsonConstants.PROCESS_TIME) ? json.get(JsonConstants.PROCESS_TIME).getAsInt()
					: 12;
			FluidStackIngredient input = FluidStackIngredient.deserialize(json.get("input"));

			List<FluidStack> outputs = (json.has(JsonConstants.OUTPUTS)
					&& json.get(JsonConstants.OUTPUTS).isJsonArray()) ? TernaryHelper.supplier(() -> {
						JsonArray array = json.getAsJsonArray(JsonConstants.OUTPUTS);
						List<FluidStack> stacks = new ArrayList<>();
						if (array.size() >= 1) {
							stacks.add(Serializers.deserializeFluid((JsonObject) array.get(0)));
							if (array.size() >= 2) {
								stacks.add(Serializers.deserializeFluid((JsonObject) array.get(1)));
							} else {
								stacks.add(FluidStack.EMPTY);
							}
						} else {
							stacks.add(FluidStack.EMPTY);
							stacks.add(FluidStack.EMPTY);
						}
						return stacks;
					}) : Lists.newArrayList(FluidStack.EMPTY, FluidStack.EMPTY);

			return new ESeparatorRecipe(pRecipeId, processTime, input, outputs.get(0), outputs.get(1));
		}

		@Override
		public ESeparatorRecipe fromNetwork(ResourceLocation pRecipeId, PacketBuffer buffer) {
			int processTime = buffer.readInt();
			FluidStackIngredient input = FluidStackIngredient.read(buffer);
			FluidStack o1 = buffer.readFluidStack();
			FluidStack o2 = buffer.readFluidStack();
			return new ESeparatorRecipe(pRecipeId, processTime, input, o1, o2);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, ESeparatorRecipe recipe) {
			buffer.writeInt(recipe.processTime);

			recipe.input.write(buffer);
			buffer.writeFluidStack(recipe.output1);
			buffer.writeFluidStack(recipe.output2);
		}

	}

}
