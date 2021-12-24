package com.matyrobbrt.matytech.api.robo_suit.module;

import javax.annotation.Nullable;

import com.matyrobbrt.lib.registry.annotation.RegisterCustomRegistry;
import com.matyrobbrt.lib.registry.annotation.RegistryHolder;
import com.matyrobbrt.matytech.api.annotation.RegisterRoboSuitModule;
import com.matyrobbrt.matytech.api.util.MatyTechRL;
import com.matyrobbrt.matytech.api.util.ModIDs;
import com.matyrobbrt.matytech.api.util.helper.CustomRegistryHelper;
import com.matyrobbrt.matytech.api.util.objects.TargetField;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

@RegistryHolder(modid = ModIDs.MATY_TECH)
public class RoboSuitModule extends ForgeRegistryEntry<RoboSuitModule> {

	private final Properties properties;

	public RoboSuitModule(final Properties properties) {
		this.properties = properties;
	}

	public static final IForgeRegistry<RoboSuitModule> REGISTRY = null;

	@RegisterRoboSuitModule("air")
	public static final RoboSuitModule AIR = new RoboSuitModule(new Properties());

	@RegisterCustomRegistry
	public static void register(final RegistryEvent.NewRegistry event) {
		// If someone calls this, make sure to not re-register
		if (REGISTRY == null) {
			CustomRegistryHelper.<RoboSuitModule>registerRegistry(new TargetField(RoboSuitModule.class, "REGISTRY"),
					RoboSuitModule.class, new MatyTechRL("robo_suit_module"));
		}
	}

	public void onServerTick(RoboSuitModuleStack moduleStack, @Nullable ItemStack stack, PlayerEntity player) {

	}

	public void onClientTick(RoboSuitModuleStack moduleStack, @Nullable ItemStack stack, PlayerEntity player) {

	}

	public void onAdded(RoboSuitModuleStack moduleStack, @Nullable ItemStack stack, @Nullable PlayerEntity player) {

	}

	public void onRemoved(RoboSuitModuleStack moduleStack, @Nullable ItemStack stack, @Nullable PlayerEntity player) {

	}

	public int getMaxStackSize() { return properties.getStackSize(); }

	public static final class Properties {

		private int stackSize = 1;

		public Properties setStackSize(int stackSize) {
			this.stackSize = stackSize;
			return this;
		}

		public int getStackSize() { return this.stackSize; }

	}

}
