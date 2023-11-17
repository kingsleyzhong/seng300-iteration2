package com.thelocalmarketplace.software;

/*
 * All possible states that a Session can be in
 * 
 * Project iteration 2 group members:
 * Aj Sallh : 30023811
 * Anthony Kostal-Vazquez : 30048301
 * Chloe Robitaille : 30022887
 * Dvij Raval : 30024340
 * Emily Kiddle : 30122331
 * Katelan NG : 30144672
 * Kingsley Zhong : 30197260
 * Nick McCamis : 30192610
 * Sua Lim : 30177039
 * Subeg CHAHAL : 30196531
 */

public enum SessionState {
    PRE_SESSION(false),// Session is not currently running
    IN_SESSION(false),// Session is currently running
    BLOCKED(false),// The Session has been blocked and cannot progress
    ADDING_BAGS(false), // User signaled they want to add bags to the bagging area
    PAY_BY_CASH(true), // User signaled they want to pay using cash (one of the pay states)
    PAY_BY_CARD(true);// User signaled they want to pay by card (one of the pay states)
    
	
	
    // This is to simplify checking if the state is a pay state
    private final boolean payState;
	private SessionState(final boolean payState) {
		this.payState = payState;
	}
	
	/*
	 * Returns true if the SessionState is a pay state (eg: pay by card, pay by cash)
	 */
	public boolean inPay() {
		return this.payState;
	}
}
