package com.matyrobbrt.matytech.api.util;

import net.minecraftforge.fluids.FluidStack;

@FunctionalInterface
public interface TransferValidator {

	/**
	 * Calculates the limit for inputting / outputting a fluid!
	 * 
	 * @param tankIndex
	 * @param fluids
	 * @param transferType
	 * @return the limit
	 */
	int getLimit(int tankIndex, FluidStack fluids, TransferType transferType);

	enum TransferType {
		EXTRACT, RECEIVE,

		/**
		 * Usually this type <strong>should not</strong> be limited
		 */
		EXTRACT_INTERNAL,

		/**
		 * Usually this type <strong>should not</strong> be limited
		 */
		RECEIVE_INTERNAL;
	}

}
