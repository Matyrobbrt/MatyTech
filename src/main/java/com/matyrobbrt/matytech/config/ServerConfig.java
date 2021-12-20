package com.matyrobbrt.matytech.config;

import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.api.config.BaseTOMLConfig;
import com.matyrobbrt.matytech.api.config.TOMLConfigBuilder;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ServerConfig extends BaseTOMLConfig {

	public static final TOMLConfigBuilder BUILDER = new TOMLConfigBuilder();
	public static ForgeConfigSpec spec;

	public static ForgeConfigSpec.ConfigValue<Integer> minPlanets;
	public static ForgeConfigSpec.ConfigValue<Integer> maxPlanets;

	public static void register() {
		/*
		 * TODO MatyTech.ANN_PROCESSOR.getModules().forEach((id, module) ->
		 * module.initConfig(Type.SERVER, BUILDER) );
		 */
		MatyTech.ANN_PROCESSOR.getModules().get(new ResourceLocation(MatyTech.MOD_ID, "jetpack"))
				.initConfig(Type.SERVER, BUILDER);
		spec = BUILDER.build();
		ModLoadingContext.get().registerConfig(Type.SERVER, ServerConfig.spec, "matytech-server.toml");
	}

}
