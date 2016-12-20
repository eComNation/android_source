package com.ecomnationmobile.library.Data;

/**
 * Created by Abhi on 16-10-2015.
 */
public class DiscountCoupon {
    private long id;

    private String code;

    private String expires_on;

    private String title;
    private double flat_rate;
    private String percentage;
    private String product_category;
    private String product_sku;
    private String purchase_limit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpires_on() {
        return expires_on;
    }

    public void setExpires_on(String expires_on) {
        this.expires_on = expires_on;
    }

    public double getFlat_rate() {
        return flat_rate;
    }

    public void setFlat_rate(double flat_rate) {
        this.flat_rate = flat_rate;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public String getPurchase_limit() {
        return purchase_limit;
    }

    public void setPurchase_limit(String purchase_limit) {
        this.purchase_limit = purchase_limit;
    }

}
