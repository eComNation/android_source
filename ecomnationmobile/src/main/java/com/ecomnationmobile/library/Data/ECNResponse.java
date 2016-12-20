package com.ecomnationmobile.library.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 16-10-2015.
 */
public class ECNResponse {
    private List<Customer> customers;
    private ArrayList<Order> orders;
    private List<Product> products;
    private List<Address> addresses;
    private PaginationInfo pagination_info;
    private List<Category> categories;
    private List<FilterAttribute> filter_attributes;
    private List<DiscountCoupon> discount_coupons;
    private List<BannerImage> banners;
    private List<String> error;
    private List<ExtendedOption> options;
    private List<ExtendedVariant> variants;
    private List<Vendor> vendors;
    private String access_token;
    private String refresh_token;
    private int expires_in;
    private String created_at;

    public String getCreated_at() {
        return created_at;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public List<DiscountCoupon> getDiscount_coupons() {
        return discount_coupons;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<FilterAttribute> getFilter_attributes() {
        return filter_attributes;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public PaginationInfo getPagination_info() {
        return pagination_info;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public List<FilterAttribute> getFilterAttributes() {
        return filter_attributes;
    }

    public List<BannerImage> getBanners() {
        return banners;
    }

    public List<ExtendedOption> getOptions() {
        return options;
    }

    public List<ExtendedVariant> getVariants() {
        return variants;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }
}
