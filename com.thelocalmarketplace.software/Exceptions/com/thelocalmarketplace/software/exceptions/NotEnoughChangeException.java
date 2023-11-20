package com.thelocalmarketplace.software.exceptions;
/**
 * Exception that occurs when there is not enough change in the machine to dispense to the customer
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
public class NotEnoughChangeException extends RuntimeException{
	
	public NotEnoughChangeException(String message) {
		super(message);
	}

}
