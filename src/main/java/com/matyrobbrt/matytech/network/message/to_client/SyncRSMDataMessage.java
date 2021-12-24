package com.matyrobbrt.matytech.network.message.to_client;

import com.matyrobbrt.lib.network.INetworkMessage;
import com.matyrobbrt.matytech.api.client.ClientRSMData;
import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModulesData;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncRSMDataMessage implements INetworkMessage {

	private RoboSuitModulesData data;

	public SyncRSMDataMessage(RoboSuitModulesData data) {
		this.data = data;
	}

	@Override
	public void handle(Context context) {
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(this, context)));
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleClient(SyncRSMDataMessage msg, NetworkEvent.Context ctx) {
		ClientRSMData.setData(msg.data);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeNbt(data.serializeNBT());
	}

	public static SyncRSMDataMessage decode(PacketBuffer buffer) {
		RoboSuitModulesData data = new RoboSuitModulesData(RoboSuitModulesData.ID);
		data.deserializeNBT(buffer.readNbt());
		return new SyncRSMDataMessage(data);
	}

}
