package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.receipt.PrintReceipt;
import com.thelocalmarketplace.software.receipt.PrintReceiptListener;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

public class PrintReceiptTest_Gold {
	private SelfCheckoutStationGold scsg;
	
    private Session session;
    
    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    private Funds fundsGold;
    private Weight weightGold;
    private PrintReceipt receiptPrinterGold;

    @Before
    public void setUp() {
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	
    	scsg = new SelfCheckoutStationGold();
    	PowerGrid.engageUninterruptiblePowerSource();
    	scsg.plugIn(PowerGrid.instance());
    	scsg.turnOn();
    	session = new Session();
    	
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);

        fundsGold = new Funds(scsg);
        weightGold = new Weight(scsg);
        receiptPrinterGold = new PrintReceipt(scsg);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testNullStation() {
    	receiptPrinterGold = new PrintReceipt(null);
    }
    
    @Test
    public void testOneItemPrintReceipt() throws OverloadedDevice {
    	scsg.printer.addPaper(512);
    	scsg.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.printReceipt();
    	scsg.printer.cutPaper();
        String testReceipt = scsg.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
    }
    
    @Test
    public void testTwoItemPrintReceipt() throws OverloadedDevice {
    	scsg.printer.addPaper(512);
    	scsg.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
    	scsg.printer.cutPaper();
        String testReceipt = scsg.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testPrintReceiptOutOffPaper() throws OverloadedDevice {
    	scsg.printer.addPaper(2);
    	scsg.printer.addInk(1024);
    	session.start();
    	assertTrue(Session.getState() == SessionState.IN_SESSION);
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
    	assertTrue(Session.getState() == SessionState.BLOCKED);
    }

    @Test
    public void testPrintReceiptOutOffInk() throws OverloadedDevice {
    	scsg.printer.addPaper(512);
    	scsg.printer.addInk(20);
    	session.start();
    	assertTrue(Session.getState() == SessionState.IN_SESSION);
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.printReceipt();
    	scsg.printer.cutPaper();
        String testReceipt = scsg.printer.removeReceipt();
        assertEquals(testReceipt, "\nItem: Sample Product Am");
        assertTrue(Session.getState() == SessionState.BLOCKED);
    }
    
    @Test
    public void testPrintReceiptReloadPaper() throws OverloadedDevice {
    	scsg.printer.addPaper(1);
    	scsg.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
    	scsg.printer.cutPaper();
    	scsg.printer.removeReceipt();
    	scsg.printer.addPaper(512);
        session.printReceipt();
        scsg.printer.cutPaper();
        String testReceipt = scsg.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testPrintReceiptReloadInk() throws OverloadedDevice {
    	scsg.printer.addPaper(512);
    	scsg.printer.addInk(20);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
    	scsg.printer.cutPaper();
    	scsg.printer.removeReceipt();
    	scsg.printer.addInk(1024);
        session.printReceipt();
        scsg.printer.cutPaper();
        String testReceipt = scsg.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testRegisterListener() throws OverloadedDevice {
    	PrinterListener stub = new PrinterListener();
    	receiptPrinterGold.register(stub);
    	scsg.printer.addPaper(512);
    	scsg.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptPrinterGold);
    	session.addItem(product);
    	session.printReceipt();
    	scsg.printer.cutPaper();
        String testReceipt = scsg.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(stub.success);
    }
    
    @Test
    public void testDeregisterListener() throws OverloadedDevice {
    	PrinterListener stub = new PrinterListener();
    	receiptPrinterGold.register(stub);
    	assertTrue(receiptPrinterGold.deregister(stub));
    }
    
    @Test
    public void testDeregisterAllListeners() throws OverloadedDevice {
    	PrinterListener stub = new PrinterListener();
    	receiptPrinterGold.register(stub);
    	receiptPrinterGold.deregisterAll();
    	assertTrue(receiptPrinterGold.listeners.isEmpty());
    }
    
	// Stub listener
	private class PrinterListener implements PrintReceiptListener {
		public boolean block = false; //Flag for running our of ink, set to false in default
		public boolean success = false; //Flag for successful printing.

		@Override
		public void notifiyOutOfPaper() {
			block = true;
		}

		@Override
		public void notifiyOutOfInk() {
			block = true;
		}

		@Override
		public void notifiyPaperRefilled() {
			block = false;
		}

		@Override
		public void notifiyInkRefilled() {
			block = false;
		}

		@Override
		public void notifiyReceiptPrinted() {
			success = true;
		}
	}
}