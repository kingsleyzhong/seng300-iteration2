package com.thelocalmarketplace.software.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for all tests in the project
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	SelfCheckoutStationSystemTest.class,
	SelfCheckoutStationLogicTest.class,
	SessionTest.class,
	ItemAddedRuleTest.class,
	FundsTest.class,
	WeightTest.class
})

public class TestSuite {

}
