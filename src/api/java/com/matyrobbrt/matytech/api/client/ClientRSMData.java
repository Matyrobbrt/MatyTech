package com.matyrobbrt.matytech.api.client;

import java.util.function.Consumer;

import com.matyrobbrt.matytech.api.robo_suit.module.RoboSuitModulesData;

public class ClientRSMData {

	private static Consumer<RoboSuitModulesData> updater;

	private static RoboSuitModulesData data;

	public static RoboSuitModulesData getData() { return data; }

	public static void setData(RoboSuitModulesData data) { ClientRSMData.data = data; }

	public static void setUpdater(Consumer<RoboSuitModulesData> updater) {
		if (ClientRSMData.updater == null) {
			ClientRSMData.updater = updater;
		}
	}

	public static Consumer<RoboSuitModulesData> getUpdater() { return updater; }
}
