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
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.receipt.PrintReceipt;
import com.thelocalmarketplace.software.receipt.PrintReceiptListener;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

public class PrintReceiptTest {
	private SelfCheckoutStationBronze scsb;
	private SelfCheckoutStationSilver scss;
	private SelfCheckoutStationGold scsg;
	
    private Session session1;
    private Session session2;
    private Session session3;
    
    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    private Funds fundsBronze;
    private Funds fundsSilver;
    private Funds fundsGold;
    
    
    private Weight weightBronze;
    private Weight weightSilver;
    private Weight weightGold;
    
    // Code added
    private PrintReceipt receiptPrinterBronze;
    private PrintReceipt receiptPrinterSilver;
    private PrintReceipt receiptPrinterGold;

    @Before
    public void setUp() {
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	
    	scsb = new SelfCheckoutStationBronze();
    	PowerGrid.engageUninterruptiblePowerSource();
    	scsb.plugIn(PowerGrid.instance());
    	scsb.turnOn();
    	session1 = new Session();
    	//SelfCheckoutStationLogic.installOn(scsb, session1);
    	
    	SelfCheckoutStationSilver scss = new SelfCheckoutStationSilver();
    	SelfCheckoutStationGold scsg = new SelfCheckoutStationGold();
    	
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
        
        fundsSilver = new Funds(scss);
        weightSilver = new Weight(scss);
        receiptPrinterSilver = new PrintReceipt(scss);
        
        fundsGold = new Funds(scsg);
        weightGold = new Weight(scsg);
        receiptPrinterGold = new PrintReceipt(scsg);
    }
    
    @Test
    public void testOneItemPrintReceipt() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(1024);
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product);
        session1.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
    }
    
    @Test
    public void testTwoItemPrintReceipt() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(1024);
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product);
    	session1.addItem(product2);
        session1.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testPrintReceiptOutOffPaper() throws OverloadedDevice {
    	scsb.printer.addPaper(2);
    	scsb.printer.addInk(1024);
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product2);
        session1.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertEquals(testReceipt, "\nItem: Sample Product 2 Amount: 1 Price: 15\n");
    }


    @Test
    public void testPrintReceiptOutOffInk() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(20);
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product);
        session1.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertEquals(testReceipt, "\nItem: Sample Product Am");
    }
    
    @Test
    public void testPrintReceiptReloadPaper() throws OverloadedDevice {
    	scsb.printer.addPaper(1);
    	scsb.printer.addInk(1024);
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product);
    	session1.addItem(product2);
        session1.printReceipt();
        scsb.printer.cutPaper();
        scsb.printer.removeReceipt();
        scsb.printer.addPaper(512);
        session1.printReceipt();
        scsb.printer.cutPaper();
        String testReceipt = scsb.printer.removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15\n"));
    }
    
    @Test
    public void testPrintReceiptReloadInk() throws OverloadedDevice {
    	scsb.printer.addPaper(512);
    	scsb.printer.addInk(20);
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product);
    	session1.addItem(product2);
        session1.printReceipt();
        scsb.printer.cutPaper();
        scsb.printer.removeReceipt();
        scsb.printer.addInk(1024);
        session1.printReceipt();
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
    	session1.start();
    	session1.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptPrinterBronze);
    	session1.addItem(product);
        session1.printReceipt();
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
    
    
	// Stub listener
	private class PrinterListener implements PrintReceiptListener {
		public boolean block = false; //Flag for running our of ink, set to false in default
		public boolean success = false; //Flag for successful printing.

		@Override
		public void notifiyOutOfPaper() {
			block = false;
		}

		@Override
		public void notifiyOutOfInk() {
			block = false;
		}

		@Override
		public void notifiyPaperRefilled() {
			block = true;
		}

		@Override
		public void notifiyInkRefilled() {
			block = true;
		}

		@Override
		public void notifiyReceiptPrinted() {
			success = true;
		}
		
	}
}
