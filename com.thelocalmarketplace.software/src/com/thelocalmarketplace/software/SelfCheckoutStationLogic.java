package com.thelocalmarketplace.software;

import java.util.ArrayList;
import java.util.HashMap;

import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.rules.ItemAddedRule;
import com.thelocalmarketplace.software.weight.Weight;

/**
 * A facade for the logic, supporting its installation on a self checkout station.
 * Allows for a database to be constructed
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

public class SelfCheckoutStationLogic {

	/**
	 * Installs an instance of the logic on the selfCheckoutStation and the session run on the station
	 * 
	 * @param scs
	 * 			The self-checkout station that the logic shall be installed on
	 * @param session
	 * 			The session that the logic shall be installed on
	 * @return
	 * 			returns an instance of the SelfCheckoutStationLogic on a SelfChekoutStation and Session
	 */
	public static SelfCheckoutStationLogic installOn(SelfCheckoutStation scs, Session session) {
		return new SelfCheckoutStationLogic(scs, session);
	}
	
	/**
	 * Constructor for the instance of logic
	 * 
	 * @param scs
	 * 			The self-checkout station that the logic is installed on
	 * @param session
	 * 			The session that the logic shall be installed on
	 */
	private SelfCheckoutStationLogic(SelfCheckoutStation scs, Session session) {
		Funds funds = new Funds(scs);
		Weight weight = new Weight(scs);
		HashMap<BarcodedProduct, Integer> barcodedItems = new HashMap<BarcodedProduct, Integer>();
		
		session.setup(barcodedItems, funds, weight);
	
		new ItemAddedRule(scs, session);
	}
}
