package com.thelocalmarketplace.software.weight;

/**
 * Listens for Weight Discrepancies and changes in the Weight class
 * 
 * Project iteration 2 group members:
 * 		Aj Sallh 				: 30023811
 *		Anthony Kostal-Vazquez 	: 30048301
 *		Chloe Robitaille 		: 30022887
 *		Dvij Raval				: 30024340
 *		Emily Kiddle 			: 30122331
 *		Katelan NG 				: 30144672
 *		Kingsley Zhong 			: 30197260
 *		Nick McCamis 			: 30192610
 *		Sua Lim 				: 30177039
 *		Subeg CHAHAL 			: 30196531
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
