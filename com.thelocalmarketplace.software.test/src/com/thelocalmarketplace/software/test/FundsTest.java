package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * Testing for the Funds class
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */

public class FundsTest {
	private SelfCheckoutStation scs;
    private Funds fund;
    private CoinValidator validator;
    private BigDecimal value;
    private BigDecimal price;
    
    @Before
    public void setUp() {
    		scs = new SelfCheckoutStation();
    		fund = new Funds(scs);
    		Currency.getInstance("CAD");
    		new ArrayList<>(Arrays.asList(BigDecimal.valueOf(0.25), BigDecimal.valueOf(1.00), BigDecimal.valueOf(2.00)));
    		validator = scs.coinValidator;
    		price = BigDecimal.valueOf(5.00);
    		fund.setPay(true);		
    }
    
    @Test (expected = IllegalArgumentException.class) 
    public void testFundsNullSCS() {
        fund = new Funds(null);
    }
   
    
    @Test (expected = InvalidActionException.class)
    public void testCoinPayInactive() {
    		fund.setPay(false);
    		value = BigDecimal.valueOf(1.00);
    		fund.new InnerListener().validCoinDetected(validator, value);
    }
    
    @Test
    public void testValidCoinPayActive() {
    		value = BigDecimal.valueOf(5.00);
    		fund.new InnerListener().validCoinDetected(validator, value);
    		assertEquals(value, fund.getPaid());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testInvalidCoin() { 		
    		value = BigDecimal.valueOf(-1);
    		fund.new InnerListener().validCoinDetected(validator, value);
    		
    }
    
    @Test 
    public void testUpdateValidPrice() {
    		fund.update(price);
    		assertEquals(price, fund.getItemsPrice());
    		assertEquals(price, fund.getAmountDue());
    }
    
    @Test (expected = IllegalDigitException.class)
    public void testUpdateInvalidePrice() {
    		fund.update(BigDecimal.valueOf(-3.00));
    }
    
    @Test
    public void turnOnPay() {
    		fund.setPay(true);
    		assertTrue(fund.isPay());
    }
    
    @Test
    public void ListenForPaid() {
    	FundListenerStub stub = new FundListenerStub();
    	fund.register(stub);
    	fund.update(price);
    	value = new BigDecimal(5);
    	fund.new InnerListener().validCoinDetected(validator, value);
    	assertTrue("Paid event called", stub.getEvents().contains("Paid"));
    }
    
    @Test(expected = SimulationException.class)
    public void invalidListener() {
    	FundListenerStub stub = null;
    	fund.register(stub);
    }
    
    @Test
    public void unRegisterListener() {
    	FundListenerStub stub = new FundListenerStub();
    	fund.register(stub);
    	fund.deregister(stub);
    	fund.update(price);
    	value = new BigDecimal(5);
    	fund.new InnerListener().validCoinDetected(validator, value);
    	assertFalse("Paid event called", stub.getEvents().contains("Paid"));
    }
    
    @Test
    public void deRegisterAllListeners() {
    	FundListenerStub stub = new FundListenerStub();
    	FundListenerStub stub2 = new FundListenerStub();
    	fund.register(stub);
    	fund.register(stub2);
    	fund.deregisterAll();
    	fund.update(price);
    	value = new BigDecimal(5);
    	fund.new InnerListener().validCoinDetected(validator, value);
    	assertFalse("Paid event called", stub.getEvents().contains("Paid"));
    	assertFalse("Paid event called", stub2.getEvents().contains("Paid"));
    }
    
    @Test
    public void forCoverage() {
 	   scs.plugIn(PowerGrid.instance());
 	   scs.turnOn();
 	   scs.coinValidator.disable();
 	   scs.coinValidator.enable();
 	   scs.coinValidator.disactivate(); 	   
 	   scs.coinValidator.activate();
    }
    
    class FundListenerStub implements FundsListener{
    	ArrayList<String> events;
    	
    	public FundListenerStub() {
    		events = new ArrayList<String>();
    	}
    	
		@Override
		public void notifyPaid() {
			events.add("Paid");
			
		}
		
		public ArrayList<String> getEvents(){
			return events;
		}
    	
    }
}