package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by Abhi on 23/7/15.
 */
public class Order {
    private long id;

    private String number;

    private String customer_email;

    private boolean is_viewed;

    private String payment_processing_charge;

    private boolean is_tax_calculated;

    private double discount;

    private double discounted_amount;

    private double shipping_charge;

    private double tax_amount;

    private double sub_total;

    private double grand_total;

    private int total_items;

    private double actual_amount;

    private String tracking_code;

    private String created_at;

    private String updated_at;

    private Address shipping_address;

    private KeyValuePair order_status;

    private Address billing_address;

    private PaymentDetail payment_detail;

    private List<OrderLineItem> order_line_items;

    private List<OrderProcessLog> order_process_logs;

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public PaymentDetail getPayment_detail() {
        return payment_detail;
    }

    public void setPayment_detail(PaymentDetail payment_detail) {
        this.payment_detail = payment_detail;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public boolean is_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(boolean is_viewed) {
        this.is_viewed = is_viewed;
    }

    public String getPayment_processing_charge() {
        return payment_processing_charge;
    }

    public void setPayment_processing_charge(String payment_processing_charge) {
        this.payment_processing_charge = payment_processing_charge;
    }

    public boolean is_tax_calculated() {
        return is_tax_calculated;
    }

    public void setIs_tax_calculated(boolean is_tax_calculated) {
        this.is_tax_calculated = is_tax_calculated;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscounted_amount() {
        return discounted_amount;
    }

    public void setDiscounted_amount(double discounted_amount) {
        this.discounted_amount = discounted_amount;
    }

    public double getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(double shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public double getTax_amount() {
        return tax_amount;
    }

    public void setTax_amount(double tax_amount) {
        this.tax_amount = tax_amount;
    }

    public double getSub_total() {
        return sub_total;
    }

    public void setSub_total(double sub_total) {
        this.sub_total = sub_total;
    }

    public double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }

    public double getActual_amount() {
        return actual_amount;
    }

    public void setActual_amount(double actual_amount) {
        this.actual_amount = actual_amount;
    }

    public String getTracking_code() {  return tracking_code;  }

    public void setTracking_code(String tracking_code) {
        this.tracking_code = tracking_code;
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

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public List<OrderLineItem> getOrder_line_items() {
        return order_line_items;
    }

    public void setOrder_line_items(List<OrderLineItem> order_line_items) {
        this.order_line_items = order_line_items;
    }

    public List<OrderProcessLog> getOrder_process_logs() {
        return order_process_logs;
    }

    public void setOrder_process_logs(List<OrderProcessLog> order_process_logs) {
        this.order_process_logs = order_process_logs;
    }

    public KeyValuePair getOrder_status() {
        return order_status;
    }

    public void setOrder_status(KeyValuePair order_status) {
        this.order_status = order_status;
    }

    public Address getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(Address shipping_address) {
        this.shipping_address = shipping_address;
    }

    public Address getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(Address billing_address) {
        this.billing_address = billing_address;
    }

    public Order(){}

    public String getNumber(){
        return this.number;
    }

    public void setNumber(String number){
        this.number = number;
    }

}
