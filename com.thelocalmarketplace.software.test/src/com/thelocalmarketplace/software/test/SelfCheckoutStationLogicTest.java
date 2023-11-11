package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;


/**
 * Unit Testing for SelfCheckoutStation logic
 * 
 * Project iteration group members:
 * 		
 */
public class SelfCheckoutStationLogicTest {
	
    private SelfCheckoutStationBronze scs;
    private Session session;
    private SelfCheckoutStationLogic logic;

    @Before
    public void setUp() {
        scs = new SelfCheckoutStationBronze();
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
