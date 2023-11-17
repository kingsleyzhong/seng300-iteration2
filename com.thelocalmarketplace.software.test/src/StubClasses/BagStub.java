package StubClasses;

/*
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

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

/*
 * This class represents a shopping bag with some associated Mass. 
 * Bags are not barcoded Items.
 * Bags may be placed in the bagging area during a Session if a customer has decided to add their own bags.
 * 
 */
public class BagStub extends Item{

	public BagStub(Mass mass) {
		super(mass);
	}

}
