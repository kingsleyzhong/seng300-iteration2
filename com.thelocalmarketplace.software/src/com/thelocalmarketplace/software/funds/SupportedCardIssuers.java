package com.thelocalmarketplace.software.funds;

/**
 * <p> Allows us to store a set of supported card issuers. In practice this is something that could be installed/updated outside of
 * a session but cannot be done within a session or the software specific to the session</p> 
 *  
 * <p>Project iteration 2 group members: </p>
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
public enum SupportedCardIssuers {
    ONE("DisasterCard"),
    TWO("Viva"),
    THREE("Canadian Depress"),
    FOUR("Detrac Debit");
    
    // This is to simplify checking if the state is a pay state
    private final String financialInstitution;
	private SupportedCardIssuers(final String financialInstitution) {
		this.financialInstitution = financialInstitution;
	}
	
	public String getIssuer() {
		return this.financialInstitution;
	}
}
