package com.thelocalmarketplace.software.funds;

import java.util.HashMap;
import java.util.Map;

import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.CardIssuer;
/**
 * Database storing CardIssuer(s) that exist
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
public class CardIssuerDatabase {
	private CardIssuerDatabase() {}	
	
	public static final Map<String, CardIssuer> CARD_ISSUER_DATABASE = new HashMap<>();
}
