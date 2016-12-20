package com.ecomnationmobile.library.Data;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Abhi on 27-10-2015.
 */
@DatabaseTable
public class OrderProcessLog {
    long id;

    private Order order;

    int order_process_status_id;

    String created_at;

    String updated_at;

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

    public int getOrder_process_status_id() {
        return order_process_status_id;
    }

    public void setOrder_process_status_id(int order_process_status_id) {
        this.order_process_status_id = order_process_status_id;
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
}
