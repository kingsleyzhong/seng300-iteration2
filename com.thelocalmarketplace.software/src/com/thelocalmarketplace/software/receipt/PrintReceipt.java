package com.thelocalmarketplace.software.receipt;

import java.util.ArrayList;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

public class PrintReceipt {
	
	public ArrayList<PrintReceiptListener> listeners = new ArrayList<>();
	private boolean isOutOfPaper = false; //Flag for running our of paper, set to false in default
	private boolean isOutOfInk = false; //Flag for running our of ink, set to false in default
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
		}

		@Override
		public void thePrinterIsOutOfInk() {
			isOutOfInk = true;
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
			if (!isOutOfInk) {
				// start printing duplicate copy of the receipt again?
				print();
			}
		}

		@Override
		public void inkHasBeenAddedToThePrinter() {
			isOutOfInk = false;
			if (!isOutOfPaper) {
				// start printing duplicate copy of the receipt again?
				print();
			}
		}
    }
    
    public void printReceipt(String formattedOrder) {
    	receipt = formattedOrder;
    	this.print();
    }
    
    private void print() {
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
    		
    		try {
    			printer.print(receipt.charAt(i));
    			++charcount;
    		} catch (EmptyDevice e) {
    			System.err.println("There is either no ink or no paper in the printer");
    		} catch (OverloadedDevice e) {
    			System.err.println("The line is too long. Add a newline");
    		}
    	}
    	
    	// If the condition is passed, then all characters were successfully printed to the receipt
    	if (charcount == (receipt.length()-1)) {
    		for(PrintReceiptListener l : listeners) {
				l.notifiyReceiptPrinted();
			}
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
		if(listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}
}
