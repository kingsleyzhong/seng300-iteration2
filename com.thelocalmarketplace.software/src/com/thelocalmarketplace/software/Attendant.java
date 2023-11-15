package com.thelocalmarketplace.software;

import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

public class Attendant {

    public void attendant() {
        // freezes screen
    }

    public void enterPassword() {
        // attendant enter password
        // unfreeze and revert to previous working weight
    }

    public void attendantFixNoItemInBaggingArea(Session session) {
        session.addBulkyItem();
    }

    public void attendantFixNoCallAddBulkyItem(AbstractSelfCheckoutStation sc, BarcodedItem item) {
        sc.baggingArea.removeAnItem(item);
    }
}
