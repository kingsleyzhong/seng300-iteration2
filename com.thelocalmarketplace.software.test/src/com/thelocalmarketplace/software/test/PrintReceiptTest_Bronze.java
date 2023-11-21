package com.thelocalmarketplace.software.test;
/*
 * Testing for print receipt on the bronze receipt printer
 * 
 * Project iteration 2 group members:
 * Aj Sallh : 30023811
 * Anthony Kostal-Vazquez : 30048301
 * Chloe Robitaille : 30022887
 * Dvij Raval : 30024340
 * Emily Kiddle : 30122331
 * Katelan NG : 30144672
 * Kingsley Zhong : 30197260
 * Nick McCamis : 30192610
 * Sua Lim : 30177039
 * Subeg CHAHAL : 30196531
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.receipt.PrintReceipt;
import com.thelocalmarketplace.software.receipt.PrintReceiptListener;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

public class PrintReceiptTest_Bronze {
	private SelfCheckoutStationBronze scsb;
	
    private Session session;
    
    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    private Funds fundsBronze;
    private Weight weightBronze;
    private PrintReceipt receiptPrinterBronze;
    
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @Before
    public void setUp() {
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	
    	scsb = new SelfCheckoutStationBronze();
    	PowerGrid.engageUninterruptiblePowerSource();
    	scsb.plugIn(PowerGrid.instance());
    	scsb.turnOn();
    	session = new Session();
    	
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        
        fundsBronze = new Funds(scsb);
        weightBronze = new Weight(scsb);
        receiptPrinterBronze = new PrintReceipt(scsb);
        
        System.setErr(new PrintStream(errContent));
    }
    
    @After
    public void restoreStreams() {
    	System.setErr(originalErr);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testNullStation() {
    	receiptPrinterBronze = new PrintReceipt(null);
    }
    
    @Test
    public void testOneItemPrintReceipt() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
    }
    
    @Test
    public void testPrintReceiptOnlyAddPaper() throws OverloadedDevice {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains(""));
        scsb.printer.addPaper(512);
        session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt2 = scsb.printer.removeReceipt();
        assertTrue(testReceipt2.contains(""));
    }
    
    @Test
    public void testPrintReceiptOnlyAddInk() throws OverloadedDevice {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains(""));
        scsb.printer.addInk(1024);
        session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt2 = scsb.printer.removeReceipt();
        assertTrue(testReceipt2.contains(""));
    }
    
    @Test
    public void testTwoItemPrintReceipt() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testPrintReceiptOutOffPaper() throws OverloadedDevice {
    	scsb.printer.addPaper(2);
    	scsb.printer.addInk(1024);
    	session.start();
    	assertTrue(Session.getState() == SessionState.IN_SESSION);
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
        assertTrue(Session.getState() == SessionState.BLOCKED);
    }

    @Test
    public void testPrintReceiptOutOffInk() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(20);
    	session.start();
    	assertTrue(Session.getState() == SessionState.IN_SESSION);
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertEquals(testReceipt, "\nItem: Sample Product Am");
        assertTrue(Session.getState() == SessionState.BLOCKED);
    }
    
    @Test
    public void testPrintReceiptReloadPaper() throws OverloadedDevice {
    	scsb.printer.addPaper(1);
    	scsb.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
        scsb.printer.cutPaper();
        scsb.printer.removeReceipt();
        scsb.printer.addPaper(512);
        session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testPrintReceiptReloadInk() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(20);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.addItem(product2);
    	session.printReceipt();
        scsb.printer.cutPaper();
        scsb.printer.removeReceipt();
        scsb.printer.addInk(1024);
        session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testRegisterListener() throws OverloadedDevice {
    	PrinterListener stub = new PrinterListener();
    	receiptPrinterBronze.register(stub);
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product);
    	session.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(stub.success);
    }
    
    @Test
    public void testDeregisterListener() throws OverloadedDevice {
    	PrinterListener stub = new PrinterListener();
    	receiptPrinterBronze.register(stub);
    	assertTrue(receiptPrinterBronze.deregister(stub));
    }
    
    @Test
    public void testDeregisterAllListeners() throws OverloadedDevice {
    	PrinterListener stub = new PrinterListener();
    	receiptPrinterBronze.register(stub);
    	receiptPrinterBronze.deregisterAll();
    	assertTrue(receiptPrinterBronze.listeners.isEmpty());
    }
    
    @Test
    public void testPrintingLineLongerThenCharLimit() throws OverloadedDevice {
    	BarcodedProduct product3 = new BarcodedProduct(barcode2, "A very long long long long long long long product name", 15, 20.0);
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(1024);
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session.addItem(product3);
    	session.printReceipt();
    	String errorMessage = errContent.toString();
    	assertTrue(errorMessage.contains("The line is too long. Add a newline"));
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