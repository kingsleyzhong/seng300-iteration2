package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;

/*
 * The intention is to have this to be merged with other tests eventually
 * Since we haven't supported the different types of SCS, this is
 * to temporarily test the code
 * 
 * 
 * Code is borrowed from SessionTest for easy merge
 */

public class SessionStateTest_TEMP {
    private SelfCheckoutStationGold scs = new SelfCheckoutStationGold();
    private Session session;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;
    private Funds funds;
    private Weight weight;

    @Before
    public void setUp() {
        session = new Session();
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        // funds = new Funds(scs);
        // weight = new Weight(scs);
    }

    @Test
    public void initialState() {
        assertEquals(Session.getState(), SessionState.PRE_SESSION);
        assertFalse(Session.getState().inPay());
    }

    @Test
    public void startSession() {
        session.start();
        assertEquals(Session.getState(), SessionState.IN_SESSION);
    }

    // This currently fails because we haven't overhauled the SCS logic
    // Useful in the future
    @Test
    public void paymentState() {
        session.addItem(product);
        session.pay();
        assertEquals(Session.getState(), SessionState.PAY_BY_CASH);
        assertTrue(Session.getState().inPay());
    }
    
    
}
