package com.matyrobbrt.matytech.init;

import static com.matyrobbrt.lib.registry.BetterRegistryObject.fluid;

import java.util.function.Supplier;

import com.matyrobbrt.lib.registry.BetterRegistryObject;
import com.matyrobbrt.lib.registry.annotation.RegisterFluid;
import com.matyrobbrt.lib.registry.annotation.RegisterItem;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.matytech.MatyTech;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

@RegistryHolder(modid = MatyTech.MOD_ID)
public class FluidInit {

	public static final ResourceLocation STILL_RL = new ResourceLocation("block/water_still");
	public static final ResourceLocation FLOWING_RL = new ResourceLocation("block/water_flow");
	public static final ResourceLocation OVERLAY_RL = new ResourceLocation("block/water_overlay");

	//@formatter:off
	@RegisterFluid("liquid_hydrogen")
	public static final BetterRegistryObject<FlowingFluid> LIQUID_HYDROGEN = fluid(() -> new ForgeFlowingFluid.Source(Properties.LIQUID_HYDROGEN_PROPERTIES));
	@RegisterFluid("liquid_hydrogen_flowing")
	public static final BetterRegistryObject<FlowingFluid> LIQUID_HYDROGEN_FLOWING = fluid(() -> new ForgeFlowingFluid.Flowing(Properties.LIQUID_HYDROGEN_PROPERTIES));
	//@formatter:on

	public static class Properties {

		public static final ForgeFlowingFluid.Properties LIQUID_HYDROGEN_PROPERTIES = new ForgeFlowingFluid.Properties(
				LIQUID_HYDROGEN, LIQUID_HYDROGEN_FLOWING, FluidAttributes.builder(STILL_RL, FLOWING_RL))
						.bucket(() -> LIQUID_HYDROGEN_BUCKET);

	}

	@RegisterItem("liquid_hydrogen_bucket")
	public static final BucketItem LIQUID_HYDROGEN_BUCKET = createBucket(LIQUID_HYDROGEN::get);

	private static BucketItem createBucket(Supplier<FlowingFluid> fluid) {
		return new BucketItem(fluid,
				new Item.Properties().tab(MatyTech.MATY_TECH_TAB).stacksTo(1).craftRemainder(Items.BUCKET));
	}

}
