package com.matyrobbrt.matytech.init;

import static com.matyrobbrt.matytech.MatyTech.MOD_ID;

import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class TagInit {

	public static final class Fluids {

		public static final ITag.INamedTag<Fluid> HYDROGEN = forge("hydrogen");

		private static ITag.INamedTag<Fluid> forge(String path) {
			return FluidTags.bind(new ResourceLocation(MOD_ID, path).toString());
		}

	}

}
