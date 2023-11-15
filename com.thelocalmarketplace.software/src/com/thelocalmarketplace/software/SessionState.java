package com.thelocalmarketplace.software;

public enum SessionState {
    PRE_SESSION(false),
    IN_SESSION(false),
    BLOCKED(false),
    PAY_BY_CASH(true),
    PAY_BY_CARD(true);
    
	
	
    // This is to simplify checking if the state is a pay state
    private final boolean payState;
	private SessionState(final boolean payState) {
		this.payState = payState;
	}
	
	public boolean inPay() {
		return this.payState;
	}
}
