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
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
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
