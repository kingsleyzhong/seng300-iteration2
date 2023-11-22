package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.funds.Funds;

public abstract class AbstractDemoTest {
	private AbstractSelfCheckoutStation scs;
	private Funds funds;
	
	// This is done this way for JUnit best practices -StackOverflow
	protected abstract AbstractSelfCheckoutStation createInstance();
	
	@Before
	public void setup() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		scs = createInstance();
		funds = new Funds(scs);
	}
	
	@Test
	public void sampleTest() {
		funds.setPay(true);
		assertTrue(funds.isPay());
	}
	
}


