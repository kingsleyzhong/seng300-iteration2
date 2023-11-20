package com.thelocalmarketplace.software.test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCashController;
import com.thelocalmarketplace.software.test.PayByCashControllerTest.MockSession;

import powerutility.PowerGrid;

public class PayByCashControllerTest {
	
	private SelfCheckoutStationBronze scs;
	private SelfCheckoutStationSilver scss;
	private SelfCheckoutStationGold scsg;
	private CoinValidator validator;
	private CoinValidator validatorSilver;
	private CoinValidator validatorGold;
	private PayByCashController cashControllerBronze;
	private PayByCashController cashControllerSilver;
	private PayByCashController cashControllerGold;
	private BigDecimal value;
	private BigDecimal price;
	private Funds fundScs;
	private Funds fundScss;
	private Funds fundScsg;
	
/***
 * setting up
 */
	
	@Before
	public void setUp() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
				
		scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		Funds fundScs = new Funds(scs);
		this.fundScs = fundScs;
		this.cashControllerBronze = new PayByCashController(scs, fundScs);
		
		scss = new SelfCheckoutStationSilver();
		scss.plugIn(PowerGrid.instance());
		scss.turnOn();
		this.fundScss = new Funds(scss);
		this.cashControllerSilver = new PayByCashController(scss, fundScss);
		
		scsg = new SelfCheckoutStationGold();
		scsg.plugIn(PowerGrid.instance());
		scsg.turnOn();
		this.fundScsg = new Funds(scsg);
		this.cashControllerGold = new PayByCashController(scss, fundScsg);
		
		
	}
	
/***
 * Insert a valid coin of value 1
 * expected for the total cash paid to update to 1	
 * @throws DisabledException
 * @throws CashOverloadException
 */
	@Test
	
	public void validCoinObserved() throws DisabledException, CashOverloadException {
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		Coin coin = new Coin(currency, value);
						
		MockSession mockSession = new MockSession();
		mockSession.pay();
		
		scs.coinValidator.receive(coin);
		Assert.assertEquals(BigDecimal.ONE, fundScs.getPaid());	
		
		scss.coinValidator.receive(coin);
		Assert.assertEquals(BigDecimal.ONE, cashControllerSilver.getCashPaid());
		
		scsg.coinValidator.receive(coin);
		Assert.assertEquals(BigDecimal.ONE, cashControllerGold.getCashPaid());
		

		
	}
/***
 * Insert a invalid coin of value 2
 * expected for the total cash paid to not update	
 * @throws DisabledException
 * @throws CashOverloadException
 */	
	@Test
	
	public void invalidCoinObserved() throws DisabledException, CashOverloadException {
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(2);
		
		Coin coin = new Coin(currency, value);
						
		MockSession mockSession = new MockSession();
		mockSession.pay();
		
		scs.coinValidator.receive(coin);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerBronze.getCashPaid());	
		
		scss.coinValidator.receive(coin);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerSilver.getCashPaid());
		
		scsg.coinValidator.receive(coin);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerGold.getCashPaid());	
	}
	
/***
 * Insert a valid coin of value 1, but pay is not active
 * expected for InvalidActionException to occur	
 * @throws DisabledException
 * @throws CashOverloadException
 */	
	@Test(expected = InvalidActionException.class)
	
	public void payInactiveBronze() throws DisabledException, CashOverloadException {
 
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		MockSession mockSession = new MockSession();
		mockSession.block();
		
		Coin coin = new Coin(currency, value);
						
		scs.coinValidator.receive(coin);
		
	}
	
/***
 * Insert a valid coin of value 1, but pay is not active
 * expected for InvalidActionException to occur	
 * @throws DisabledException
 * @throws CashOverloadException
 */		
	@Test(expected = InvalidActionException.class)
	
	public void payInactiveSilver() throws DisabledException, CashOverloadException {
 
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		MockSession mockSession = new MockSession();
		mockSession.block();
		
		Coin coin = new Coin(currency, value);
						
		scss.coinValidator.receive(coin);
		
	}
	
/***
 * Insert a valid coin of value 1, but pay is not active
 * expected for InvalidActionException to occur	
 * @throws DisabledException
 * @throws CashOverloadException
 */		
	@Test(expected = InvalidActionException.class)
	
	public void payInactiveGold() throws DisabledException, CashOverloadException {
 
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		MockSession mockSession = new MockSession();
		mockSession.block();
		
		Coin coin = new Coin(currency, value);
						
		scsg.coinValidator.receive(coin);
		
	}
	
/***
 * Insert a valid banknote of value 1
 * expected for the total cash paid to update to 1	
 * @throws DisabledException
 * @throws CashOverloadException
 */
	@Test
	
	public void validBanknoteObserved() throws DisabledException, CashOverloadException {
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		Banknote note = new Banknote(currency, value);
		
		MockSession mockSession = new MockSession();
		mockSession.pay();
		
		scs.banknoteValidator.receive(note);
		Assert.assertEquals(BigDecimal.ONE, cashControllerBronze.getCashPaid());	
		
		scss.banknoteValidator.receive(note);
		Assert.assertEquals(BigDecimal.ONE, cashControllerSilver.getCashPaid());
		
		scsg.banknoteValidator.receive(note);
		Assert.assertEquals(BigDecimal.ONE, cashControllerGold.getCashPaid());

	}
	
/***
 * Insert a invalid banknote of value 2
 * expected for the total cash paid to not update 	
 * @throws DisabledException
 * @throws CashOverloadException
 */	
	@Test
	
	public void invalidBanknoteObserved() throws DisabledException, CashOverloadException {
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(2);
		
		Banknote note = new Banknote(currency, value);
						
		MockSession mockSession = new MockSession();
		mockSession.pay();
		
		scs.banknoteValidator.receive(note);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerBronze.getCashPaid());	
		
		scss.banknoteValidator.receive(note);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerSilver.getCashPaid());
		
		scsg.banknoteValidator.receive(note);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerGold.getCashPaid());	
	}

/***
 * Insert a valid banknote of value 1, but pay is not active
 * expected for InvalidActionException to occur	
 * @throws DisabledException
 * @throws CashOverloadException
 */		
	@Test(expected = InvalidActionException.class)
	
	public void payBanknoteInactiveBronze() throws DisabledException, CashOverloadException {
 
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		Banknote note = new Banknote(currency, value);
		
		MockSession mockSession = new MockSession();
		mockSession.block();
								
		scs.banknoteValidator.receive(note);
		
	}
	
/***
 * Insert a valid banknote of value 1, but pay is not active
 * expected for InvalidActionException to occur	
 * @throws DisabledException
 * @throws CashOverloadException
 */			
	@Test(expected = InvalidActionException.class)
	
	public void payBanknoteInactiveSilver() throws DisabledException, CashOverloadException {
 
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		Banknote note = new Banknote(currency, value);
		
		MockSession mockSession = new MockSession();
		mockSession.block();
								
		scss.banknoteValidator.receive(note);
		
	}
	
/***
 * Insert a valid banknote of value 1, but pay is not active
 * expected for InvalidActionException to occur	
 * @throws DisabledException
 * @throws CashOverloadException
 */			
	@Test(expected = InvalidActionException.class)
	
	public void payBanknoteInactiveGold() throws DisabledException, CashOverloadException {
 
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);
		
		Banknote note = new Banknote(currency, value);
		
		MockSession mockSession = new MockSession();
		mockSession.block();
						
		scsg.banknoteValidator.receive(note);
		
	}	
	
/***
 * Mock Session to make the session pay mode in Pay by Cash
 */
	public class MockSession extends Session {
		
		@Override
		public void pay() {
			sessionState = SessionState.PAY_BY_CASH;
		}
		
		public void block() {
			sessionState = SessionState.BLOCKED;
		}
	}
	
}
