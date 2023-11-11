package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;

import java.util.HashMap;


/**
 * Unit Testing for SelfCheckoutStation logic
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
public class SelfCheckoutStationLogicTest {
	
    private SelfCheckoutStation scs;
    private Session session;
    private SelfCheckoutStationLogic logic;

    @Before
    public void setUp() {
        scs = new SelfCheckoutStation();
        session = new Session();
        logic = SelfCheckoutStationLogic.installOn(scs, session);
    }

    @Test
    public void testInstallation() {
        assertNotNull(logic);
    }

    @Test
    public void testInstallationComponents() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the session and scs.
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }
}
