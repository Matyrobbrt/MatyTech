package com.matyrobbrt.matytech.api.robo_suit.module;

import java.util.LinkedHashMap;
import java.util.UUID;

import javax.annotation.WillNotClose;

import com.matyrobbrt.matytech.api.client.ClientRSMData;
import com.matyrobbrt.matytech.api.util.ModIDs;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import net.minecraftforge.common.util.INBTSerializable;

public class RoboSuitModulesData extends WorldSavedData {

	public static final String ID = ModIDs.MATY_TECH + "_robo_suit_modules";

	private final MIMap moduleInventories = new MIMap();

	public RoboSuitModulesData(String id) {
		super(id);
	}

	public static RoboSuitModulesData getDefaultInstance(World level) {
		if (!level.isClientSide()) {
			return getDefaultInstance(level.getServer());
		} else {
			return ClientRSMData.getData();
		}
	}

	public static RoboSuitModulesData getDefaultInstance(@WillNotClose MinecraftServer server) {
		ServerWorld w = server.getLevel(World.OVERWORLD);
		return w.getDataStorage().computeIfAbsent(() -> new RoboSuitModulesData(ID), ID);
	}

	public RoboSuitModuleStack addModule(UUID uuid, RoboSuitModuleStack moduleStack, ItemStack container,
			boolean simulate) {
		RoboSuitModuleStack toRet = getOrCreateInventory(uuid).addModule(moduleStack, container, simulate);
		setDirty();
		return toRet;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.put("ModuleInventories", moduleInventories.serializeNBT());
		return nbt;
	}

	@Override
	public void load(CompoundNBT nbt) {
		moduleInventories.deserializeNBT(nbt.getCompound("ModuleInventories"));
	}

	@Override
	public void setDirty() {
		super.setDirty();
		syncClients();
	}

	public void syncClients() {
		ClientRSMData.getUpdater().accept(this);
	}

	public ModuleInventory getOrCreateInventory(UUID uuid) {
		if (moduleInventories.containsKey(uuid)) {
			return moduleInventories.get(uuid);
		} else {
			ModuleInventory inv = new ModuleInventory();
			moduleInventories.put(uuid, inv);
			return inv;
		}
	}

	public boolean isEmpty() { return moduleInventories.isEmpty(); }

	public static class MIMap extends LinkedHashMap<UUID, ModuleInventory>
			implements INBTSerializable<CompoundNBT> {

		private static final long serialVersionUID = -4743689970110666287L;

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT tag = new CompoundNBT();
			entrySet().forEach(entry -> tag.put(entry.getKey().toString(), entry.getValue().serializeNBT()));
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			nbt.getAllKeys().forEach(id -> put(UUID.fromString(id), ModuleInventory.fromNBT(nbt.getCompound(id))));
		}

		@Override
		public boolean equals(Object o) {
			return super.equals(o) && o instanceof MIMap;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

}
