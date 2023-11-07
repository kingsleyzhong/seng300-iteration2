package com.thelocalmarketplace.software.exceptions;

/**
 * Exception that occurs when an action in unable to occur due to 
 * the shopping cart being empty.
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */
@SuppressWarnings("serial")
public class CartEmptyException extends InvalidActionException{
	/**
	 * Basic constructor
	 * 
	 * @param message
	 * 			An explanatory message of the problem
	 */
	public CartEmptyException(String message) {
		super(message);
	}
}
