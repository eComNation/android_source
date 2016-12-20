package com.ecomnationmobile.library.Data;

/**
 * Created by Abhi on 16-10-2015.
 */
public class PaginationInfo {

    private int current_page;
    private int next_page;

    public int getPrevious_page() {
        return previous_page;
    }

    public void setPrevious_page(int previous_page) {
        this.previous_page = previous_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getNext_page() {
        return next_page;
    }

    public void setNext_page(int next_page) {
        this.next_page = next_page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_entries() {
        return total_entries;
    }

    public void setTotal_entries(int total_entries) {
        this.total_entries = total_entries;
    }

    private int previous_page;
    private int total_pages;
    private int total_entries;
}
