package com.thelocalmarketplace.software.receipt;
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
public interface PrintReceiptListener {

	/**
	 * Signals an event that the printer is out of paper
	 */
	void notifiyOutOfPaper();
	
	/**
	 * Signals an event that the printer is out of ink
	 */
	void notifiyOutOfInk();
	
	/**
	 * Signals an event that the printer paper has been refilled
	 */
	void notifiyPaperRefilled();
	
	/**
	 * Signals an event that the ink has been refilled
	 */
	void notifiyInkRefilled();
	
	/**
	 * Signals that the receipt was successfully printed 
	 */
	void notifiyReceiptPrinted();

}
