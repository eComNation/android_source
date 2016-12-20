package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by Abhi on 21-10-2015.
 */
public class OrderLineItem {
    private long id;

    private String name;

    private String sku;

    private double actual_price;

    private double discount;

    private Order order;

    private long variant_id;

    private String cart_id;

    private int quantity;

    private Integer max_quantity_possible;

    private double discounted_price;

    private String created_at;

    private String updated_at;

    private String image_url;

    private List<CustomDetail> custom_data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public long getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(long variant_id) {
        this.variant_id = variant_id;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getMax_quantity_possible() {
        return max_quantity_possible;
    }

    public double getDiscounted_price() {
        return discounted_price;
    }

    public void setDiscounted_price(double discounted_price) {
        this.discounted_price = discounted_price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getActual_price() {
        return actual_price;
    }

    public void setActual_price(double actual_price) {
        this.actual_price = actual_price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<CustomDetail> getCustom_details() {
        return custom_data;
    }

    public void setCustom_details(List<CustomDetail> custom_details) {
        this.custom_data = custom_details;
    }
}
