package StubClasses;

import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;

/**
 * <p> A Stub class for the funds class test that allows for the sessionState to be controlled with ease without having to 
 * create a full session full of unnecessary code and requirements</p> 
 *  
 * <p>Project iteration 2 group members: </p>
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

public class SessionFundsSimulationStub extends Session {
	public void setPayByCash() {
		sessionState = SessionState.PAY_BY_CASH;
	}
	
	public void setPayByCard() {
		sessionState = SessionState.PAY_BY_CARD;
	}
		
	public void block() {
		sessionState = SessionState.BLOCKED;
	}
}
