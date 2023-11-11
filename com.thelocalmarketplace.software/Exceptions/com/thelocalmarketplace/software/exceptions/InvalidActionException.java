package com.thelocalmarketplace.software.exceptions;

/**
 * An exception that can be raised when the behaviour within the software is
 * not able to be used. For example, this can occur when an item is attempted to be
 * added when the session is frozen. Thus, this is an invalidAction to occur.
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
@SuppressWarnings("serial")
public class InvalidActionException extends RuntimeException{
	/**
	 * Basic constructor
	 * 
	 * @param message
	 * 			An explanatory message of the problem
	 */
	public InvalidActionException(String message) {
		super(message);
	}
}
