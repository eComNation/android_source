package com.ecomnationmobile.library.Data;

import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by Abhi on 03-09-2015.
 */
@DatabaseTable
public class Product {
    private long id;

    private String name;

    private String category;

    private long category_id;

    private int quantity;

    private int minimum_stock_level;

    private String product_image_url;

    private String image_url;

    private double price;

    private String sku;

    private String permalink;

    private boolean track_quantity;

    private double previous_price;

    private boolean is_discounted;

    private double discount;

    private String description;

    private String detailed_description;

    private Variant default_variant;

    private List<ProductImage> images;

    private List<Variant> variants;

    private List<KeyValuePair> options;

    private List<ProductAttribute> filter_attributes;

    private List<Product> related_products;

    public int getMinimum_stock_level() {
        return minimum_stock_level;
    }

    public List<Product> getRelated_products() {
        return related_products;
    }

    public List<ProductAttribute> getFilter_attributes() {
        return filter_attributes;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrevious_price() {
        return previous_price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isTrack_quantity() {
        return track_quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct_image_url() {
        return product_image_url;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public List<KeyValuePair> getOptions() {
        return options;
    }

    public void setOptions(List<KeyValuePair> options) {
        this.options = options;
    }

    public Variant getDefault_variant() {
        return default_variant;
    }

    public long getCategory_id() {
        return category_id;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDetailed_description() {
        return detailed_description;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}
