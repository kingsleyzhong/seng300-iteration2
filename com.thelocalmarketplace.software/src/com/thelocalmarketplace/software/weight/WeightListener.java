package com.thelocalmarketplace.software.weight;

/**
 * Listens for Weight Discrepancies and changes in the Weight class
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */

public interface WeightListener {
	
	/**
	 * Signals an event in which a Discrepancy has occurred
	 * 
	 * May be modified in the future to check if the discrepancy was caused by adding/removing an item
	 * to the cart, or adding/removing an item to the scale
	 */
	void notifyDiscrepancy();
	
	/**
	 * Signals an event in which a previous Discrepancy has been resolved
	 */
	void notifyDiscrepancyFixed();
}
