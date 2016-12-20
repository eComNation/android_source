package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by Abhi on 07-03-2016.
 */
public class Cart {
    long id;
    String token;
    long discount_coupon_id;
    String created_at;
    String updated_at;
    List<OrderLineItem> items;
    int update_index;
    double discounted_cart_amount;
    double gift_card_amount;
    int reward_points;
    int points_per_unit_amount;
    int available_reward_points;
    boolean group_not_excluded;

    public int getReward_points() {
        return reward_points;
    }

    public void setReward_points(int reward_points) {
        this.reward_points = reward_points;
    }

    public int getPoints_per_unit_amount() {
        return points_per_unit_amount;
    }

    public int getAvailable_reward_points() {
        return available_reward_points;
    }

    public void setAvailable_reward_points(int available_reward_points) {
        this.available_reward_points = available_reward_points;
    }

    public boolean isGroup_not_excluded() {
        return group_not_excluded;
    }

    public double getDiscounted_cart_amount() {
        return discounted_cart_amount;
    }

    public double getGift_card_amount() {
        return gift_card_amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getDiscount_coupon_id() {
        return discount_coupon_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public List<OrderLineItem> getItems() {
        return items;
    }

    public void setItems(List<OrderLineItem> items) {
        this.items = items;
    }

    public int getUpdate_id() {
        return update_index;
    }

    public void setUpdate_id(int update_id) {
        this.update_index = update_id;
    }
}
