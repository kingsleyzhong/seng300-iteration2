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
public class SelfCheckoutStationLogicTest {

    private SelfCheckoutStationBronze scsb;
    private SelfCheckoutStationSilver scss;
    private SelfCheckoutStationGold scsg;
    private Session session;
    private SelfCheckoutStationLogic logic;

    @Before
    public void setUp() {
    	// updated to Hardware 2.2 and fixed to make it a static call
    	SelfCheckoutStationBronze.resetConfigurationToDefaults();
    	SelfCheckoutStationSilver.resetConfigurationToDefaults();
    	SelfCheckoutStationGold.resetConfigurationToDefaults();
    	
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
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the
        // session and scsb.
        SelfCheckoutStationLogic.installOn(scsb, session);
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }

    @Test
    public void testInstallationComponentsSilver() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the
        // session and scsb.
        SelfCheckoutStationLogic.installOn(scss, session);
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }

    @Test
    public void testInstallationComponentsGold() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the
        // session and scsb.
        SelfCheckoutStationLogic.installOn(scsg, session);
        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }
}
