package com.matyrobbrt.matytech.api.util;

public class NumberUtils {

	public static boolean isMouseBetween(int mouseX, int mouseY, int startX, int startY, int width, int height) {
		return numberIsBetween(mouseX, startX, startX + width) && numberIsBetween(mouseY, startY, startY + height);
	}

	public static boolean numberIsBetween(int number, int c1, int c2) {
		return number <= c2 && number >= c1;
	}

}
