package StubClasses;

import java.util.ArrayList;

import com.thelocalmarketplace.software.funds.FundsListener;

/**
 * <p> A Stub class for the funds test class that is used to listen for payments done with both cash and card </p> 
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

public class FundsListenerStub implements FundsListener {
	ArrayList<String> events;

	public FundsListenerStub() {
		events = new ArrayList<String>();
	}

	@Override
	public void notifyPaid() {
		events.add("Paid");

	}

	public ArrayList<String> getEvents() {
		return events;
	}
}
