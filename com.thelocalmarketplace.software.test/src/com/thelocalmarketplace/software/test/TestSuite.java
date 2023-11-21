package com.thelocalmarketplace.software.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for all tests in the project
 * 
 * Project iteration 2 group members:
 * 		Aj Sallh 				: 30023811
 *		Anthony Kostal-Vazquez 	: 30048301
 *		Chloe Robitaille 		: 30022887
 *		Dvij Raval				: 30024340
 *		Emily Kiddle 			: 30122331
 *		Katelan NG 				: 30144672
 *		Kingsley Zhong 			: 30197260
 *		Nick McCamis 			: 30192610
 *		Sua Lim 				: 30177039
 *		Subeg CHAHAL 			: 30196531
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AddBagsTest_Bronze.class,
	AddBagsTest_Gold.class,
	AddBagsTest_Silver.class,
	AddBulkyItemTest.class,
	FundsTest.class,
	ItemAddedRuleTest.class,
	PayByCardTest_Bronze.class,
	PayByCardTest_Silver.class,
	PayByCardTest_Gold.class,
	PayByCashControllerTest.class,
	PrintReceiptTest_Bronze.class,
	PrintReceiptTest_Gold.class,
	PrintReceiptTest_Silver.class,
	RemoveItemTests.class,
	SelfCheckoutStationLogicTest.class,
	SelfCheckoutStationSystemTest.class,
	SessionTest.class,
	WeightTest.class,
})

public class TestSuite {

}
