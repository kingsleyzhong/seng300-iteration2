package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

public class SilverDemoTest extends AbstractDemoTest {
	
	@Override
	protected AbstractSelfCheckoutStation createInstance() {
		return new SelfCheckoutStationSilver();
	}
}