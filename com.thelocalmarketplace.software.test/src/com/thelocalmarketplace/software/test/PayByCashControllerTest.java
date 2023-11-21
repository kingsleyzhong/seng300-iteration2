package com.thelocalmarketplace.software.test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
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
	private stubCVListener cvListenerBronze,cvListenerSilver,cvListenerGold;
	private stubBVListener bvListenerBronze,bvListenerSilver,bvListenerGold;
	
	
/***
 * setting up
 */
	
	@Before
	public void setUp() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
				
		scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		this.fundScs = new Funds(scs);
		this.cashControllerBronze = new PayByCashController(scs, fundScs);
		
		// engage uninteruptable power source
		PowerGrid.instance().engageUninterruptiblePowerSource();
		
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
		
		// register listeners
		cvListenerBronze = new stubCVListener();
		scs.coinValidator.attach(cvListenerBronze);
		cvListenerSilver = new stubCVListener();
		scss.coinValidator.attach(cvListenerSilver);		
		cvListenerGold = new stubCVListener();
		scsg.coinValidator.attach(cvListenerGold);
		
		bvListenerBronze = new stubBVListener();
		scs.banknoteValidator.attach(bvListenerBronze);
		bvListenerSilver = new stubBVListener();
		scss.banknoteValidator.attach(bvListenerSilver);
		bvListenerGold = new stubBVListener();
		scsg.banknoteValidator.attach(bvListenerGold);
		
		this.price = BigDecimal.TEN;
		
		fundScs.update(price);
		fundScss.update(price);
		fundScsg.update(price);

	}
	
	@After
	public void tearDown() {
		// clear all listeners
		scs.coinValidator.detachAll();
		scss.coinValidator.detachAll();
		scsg.coinValidator.detachAll();

		scs.banknoteValidator.detachAll();
		scss.banknoteValidator.detachAll();
		scsg.banknoteValidator.detachAll();
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
		mockSession.payByCash();
		
		while(!cvListenerBronze.coinIsValid) {
			scs.coinSlot.receive(coin);
		}
		Assert.assertEquals(BigDecimal.ONE, fundScs.getPaid());	
		
		while(!cvListenerSilver.coinIsValid) {
			scss.coinSlot.receive(coin);
		}
		Assert.assertEquals(BigDecimal.ONE, cashControllerSilver.getCashPaid());
		
		while(!cvListenerGold.coinIsValid) {
			scsg.coinSlot.receive(coin);
		}
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
		mockSession.payByCash();
		
		scs.coinSlot.receive(coin);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerBronze.getCashPaid());	
		
		scss.coinSlot.receive(coin);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerSilver.getCashPaid());
		
		scsg.coinSlot.receive(coin);
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
						
		while(!cvListenerBronze.coinIsValid) {
			scs.coinSlot.receive(coin);
		}
		
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
		
		while(!cvListenerSilver.coinIsValid) {
			scss.coinSlot.receive(coin);
		}
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
						
		while(!cvListenerGold.coinIsValid) {
			scsg.coinSlot.receive(coin);
		}
		
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
		mockSession.payByCash();
		
		while(!bvListenerBronze.cashIsValid) {
			if(scs.banknoteInput.hasDanglingBanknotes()) {
				scs.banknoteInput.removeDanglingBanknote();
			}
			scs.banknoteInput.receive(note);
		}		
		Assert.assertEquals(BigDecimal.ONE, cashControllerBronze.getCashPaid());	
		
		while(!bvListenerSilver.cashIsValid) {
			if(scss.banknoteInput.hasDanglingBanknotes()) {
				scss.banknoteInput.removeDanglingBanknote();
			}
			scss.banknoteInput.receive(note);
		}
		Assert.assertEquals(BigDecimal.ONE, cashControllerSilver.getCashPaid());
		
		while(!bvListenerGold.cashIsValid) {
			if(scsg.banknoteInput.hasDanglingBanknotes()) {
				scsg.banknoteInput.removeDanglingBanknote();
			}
			scsg.banknoteInput.receive(note);
		}
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
		mockSession.payByCash();
		
		scs.banknoteInput.receive(note);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerBronze.getCashPaid());	
		
		scss.banknoteInput.receive(note);
		Assert.assertEquals(BigDecimal.ZERO, cashControllerSilver.getCashPaid());
		
		scsg.banknoteInput.receive(note);
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
		
		while(!bvListenerBronze.cashIsValid) {
			if(scs.banknoteInput.hasDanglingBanknotes()) {
				scs.banknoteInput.removeDanglingBanknote();
			}
			scs.banknoteInput.receive(note);
		}
		
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
								
		while(!bvListenerSilver.cashIsValid) {
			if(scss.banknoteInput.hasDanglingBanknotes()) {
				scss.banknoteInput.removeDanglingBanknote();
			}
			scss.banknoteInput.receive(note);
		}		
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
		
		while(!bvListenerGold.cashIsValid) {
			if(scsg.banknoteInput.hasDanglingBanknotes()) {
				scsg.banknoteInput.removeDanglingBanknote();
			}
			scsg.banknoteInput.receive(note);
		}
	}	
	
/***
 * Mock Session to make the session pay mode in Pay by Cash
 */
	public class MockSession extends Session {
		
		@Override
		public void payByCash() {
			sessionState = SessionState.PAY_BY_CASH;
		}
		
		public void block() {
			sessionState = SessionState.BLOCKED;
		}
	}
	
	/**
	 * a Stub of the coin validator listener to check if coins have been "seen" or not
	 */
	public class stubCVListener implements CoinValidatorObserver{
		public boolean coinIsValid = false;
		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {

		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {

		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {			
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			coinIsValid = true;
		}

		@Override
		public void invalidCoinDetected(CoinValidator validator) {
			coinIsValid = false;

		}
	}
	
	/**
	 * a stub that listens to see if a valid banknote has been detected or not
	 */
	public class stubBVListener implements BanknoteValidatorObserver{
		public boolean cashIsValid = false;
		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {	
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
			cashIsValid = true;
		}

		@Override
		public void badBanknote(BanknoteValidator validator) {
			cashIsValid = false;		
		}
	}
}
