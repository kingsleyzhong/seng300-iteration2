package com.thelocalmarketplace.software.receipt;

import java.util.ArrayList;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
/*
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
public class PrintReceipt {
	
	public ArrayList<PrintReceiptListener> listeners = new ArrayList<>();
	private boolean isOutOfPaper = false; //Flag for running our of paper, set to false in default
	private boolean isOutOfInk = false; //Flag for running our of ink, set to false in default
	private boolean duplicateNeeded = false; //Flag for if the receipt was not printed out fully and a duplicate is needed.
	private String receipt; //The receipt that should be printed
	private IReceiptPrinter printer; //The printer associated with the session;

	
	/**
     * Constructor that initializes the funds and registers an inner listener to the self-checkout station.
     * 
     * @param scs The self-checkout station
     */
    public PrintReceipt (AbstractSelfCheckoutStation scs) {
        if (scs == null) {
            throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
        }
        InnerListener listener = new InnerListener();
        scs.printer.register(listener);
        this.printer = scs.printer;
    }
    
    /**
     * Inner class to listen for valid coin additions and update the paid amount.
     */
    public class InnerListener implements ReceiptPrinterListener {

		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
		}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
		}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
		}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
		}

		@Override
		public void thePrinterIsOutOfPaper() {
			isOutOfPaper = true;
			duplicateNeeded = true;
		}

		@Override
		public void thePrinterIsOutOfInk() {
			isOutOfInk = true;
			duplicateNeeded = true;
		}

		@Override
		public void thePrinterHasLowInk() {
			// TODO Auto-generated method stub
		}

		@Override
		public void thePrinterHasLowPaper() {
			// TODO Auto-generated method stub
		}

		@Override
		public void paperHasBeenAddedToThePrinter() {
			isOutOfPaper = false;
			for(PrintReceiptListener l : listeners) {
				l.notifiyPaperRefilled();
			}
			if (!isOutOfInk && duplicateNeeded) {
				// start printing duplicate copy of the receipt again if one is needed
				print();
			}
		}

		@Override
		public void inkHasBeenAddedToThePrinter() {
			isOutOfInk = false;
			for(PrintReceiptListener l : listeners) {
				l.notifiyInkRefilled();
			}
			if (!isOutOfPaper && duplicateNeeded) {
				// start printing duplicate copy of the receipt again if one is needed
				print();
			}
		}
    }
    
    public void printReceipt(String formattedOrder) {
    	receipt = formattedOrder;
    	this.print();
    }
    
    private void print() {
    	try {
    		printer.print('\n'); // Ensures any new receipt being printed startes on a fresh line
        	int charcount = 0;
        	for (int i = 0, n = receipt.length() ; i < n ; i++) {
        		// Notify and break out of the printing loop if
        		if (isOutOfPaper) {
        			for(PrintReceiptListener l : listeners) {
        				l.notifiyOutOfPaper();
        			}
        			break;
        		}
        		if (isOutOfInk) {
        			for(PrintReceiptListener l : listeners) {
        				l.notifiyOutOfInk();
        			}
        			break;
        		}
        		// Send the character to the printer to print
    			printer.print(receipt.charAt(i));
    			charcount++;
        	}
        	
        	// If the condition is passed, then all characters were successfully printed to the receipt
        	if (charcount == receipt.length()) {
        		for(PrintReceiptListener l : listeners) {
    				l.notifiyReceiptPrinted();
    			}
        	}
        // The empty device exception should never run as it requires printer.print() to be called when 
        // the printer is out of paper or ink, but that can never occur in this method as if isOutOfPaper or
        // isOutOfInk is true, then a call to printer.print() is never made.
    	} catch (EmptyDevice e) {
			System.err.println("There is either no ink or no paper in the printer");
		} catch (OverloadedDevice e) {
			System.err.println("The line is too long. Add a newline");
		}
    }
    
    
    
    /**
     * Methods for adding listeners to the PrintReceipt
     */
	public synchronized boolean deregister(PrintReceiptListener listener) {
		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(PrintReceiptListener listener) {
		listeners.add(listener);
	}
}
