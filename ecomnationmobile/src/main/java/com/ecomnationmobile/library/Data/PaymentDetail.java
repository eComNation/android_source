package com.ecomnationmobile.library.Data;

/**
 * Created by Abhi on 04-11-2015.
 */
public class PaymentDetail {
    private long id;

    private String payment_mode;

    private String custom_payment_detail;

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getCustom_payment_detail() {
        return custom_payment_detail;
    }

    public void setCustom_payment_detail(String custom_payment_detail) {
        this.custom_payment_detail = custom_payment_detail;
    }
}
