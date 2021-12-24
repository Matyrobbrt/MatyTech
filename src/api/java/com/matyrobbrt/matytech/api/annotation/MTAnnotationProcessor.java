package com.matyrobbrt.matytech.api.annotation;

import static java.util.Optional.of;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matyrobbrt.lib.registry.annotation.AnnotationProcessor;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModule;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class MTAnnotationProcessor extends AnnotationProcessor {

	public static final Map<String, List<RoboSuitModule>> ROBO_SUIT_MODULES = new HashMap<>();

	public MTAnnotationProcessor(String modid) {
		super(modid);
	}

	@Override
	public void register(IEventBus modBus) {
		super.register(modBus);
		addListenerIfNotIgnored(RoboSuitModule.class, this::registerRoboSuitModules);
	}

	private void registerRoboSuitModules(final RegistryEvent.Register<RoboSuitModule> event) {
		registerFieldsWithAnnotation(event, RegisterRoboSuitModule.class, RegisterRoboSuitModule::value, of(ROBO_SUIT_MODULES));
	}

}
