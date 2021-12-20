package com.matyrobbrt.matytech.config;

import com.matyrobbrt.matytech.MatyTech;
import com.matyrobbrt.matytech.api.config.BaseTOMLConfig;
import com.matyrobbrt.matytech.api.config.TOMLConfigBuilder;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class CommonConfig extends BaseTOMLConfig {

	public static final TOMLConfigBuilder BUILDER = new TOMLConfigBuilder();
	public static ForgeConfigSpec spec;

	public static ForgeConfigSpec.ConfigValue<Integer> minPlanets;
	public static ForgeConfigSpec.ConfigValue<Integer> maxPlanets;

	//@formatter:off
	public static void register() {
		MatyTech.ANN_PROCESSOR.getModules().forEach((id, module) -> 
			module.initConfig(Type.COMMON, BUILDER)
		);
		spec = BUILDER.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, CommonConfig.spec, "matytech-common.toml");
    }

}
