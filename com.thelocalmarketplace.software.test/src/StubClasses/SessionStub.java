package StubClasses;

import com.jjjwelectronics.Mass;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.funds.Funds;


/**
 * <p> A Stub class for payment method test that allow for the sessionState to be controlled with ease without having to 
 * create a full session full of unnecessary code and requirements</p> 
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
public class SessionStub extends Session{
	public static SessionState sessionState;
	
	@Override
	public void payByCard() {
		sessionState = SessionState.PAY_BY_CARD;
		} 
	
	@Override
	public void start() {
		sessionState = SessionState.IN_SESSION;
		} 
}
