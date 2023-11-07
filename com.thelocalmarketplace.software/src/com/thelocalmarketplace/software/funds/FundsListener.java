package com.thelocalmarketplace.software.funds;

/**
 * Listens for if payment has been completed
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */
public interface FundsListener {

	/**
	 * Signals an event in which the customer has paid for the complete amount of the order
	 */
	void notifyPaid();
}
