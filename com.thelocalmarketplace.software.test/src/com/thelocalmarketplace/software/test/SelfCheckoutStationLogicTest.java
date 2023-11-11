package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;


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
	
    private SelfCheckoutStationBronze scsb;
    private SelfCheckoutStationSilver scss;
    private SelfCheckoutStationGold scsg;
    private Session session;
    private SelfCheckoutStationLogic logic;

    @Before
    public void setUp() {
        scsb = new SelfCheckoutStationBronze();
        scss = new SelfCheckoutStationSilver();
        scsg = new SelfCheckoutStationGold();
        session = new Session();
    }

    @Test
    public void testInstallation() {
    	logic = SelfCheckoutStationLogic.installOn(scsb, session);
        assertNotNull(logic);
    }

    @Test
    public void testInstallationComponentsBronze() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the session and scsb.
    	SelfCheckoutStationLogic.installOn(scsb, session);
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }
    
    @Test
    public void testInstallationComponentsSilver() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the session and scsb.
    	SelfCheckoutStationLogic.installOn(scss, session);
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }
    
    @Test
    public void testInstallationComponentsGold() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the session and scsb.
    	SelfCheckoutStationLogic.installOn(scsg, session);
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }
}
