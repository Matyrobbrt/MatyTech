package com.matyrobbrt.matytech.api.robo_suit.module;

import java.util.UUID;

import net.minecraft.item.ItemStack;

public interface IRoboSuitModuleHolder {

	UUID getStackModulesUUID(ItemStack stack);

}
