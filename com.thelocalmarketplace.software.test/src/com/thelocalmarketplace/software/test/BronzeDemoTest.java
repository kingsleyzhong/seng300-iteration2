package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

public class BronzeDemoTest extends AbstractDemoTest {
	
	@Override
	protected AbstractSelfCheckoutStation createInstance() {
		return new SelfCheckoutStationBronze();
	}
}