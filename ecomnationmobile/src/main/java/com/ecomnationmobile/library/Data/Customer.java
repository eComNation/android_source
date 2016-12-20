package com.ecomnationmobile.library.Data;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Abhi on 05-09-2015.
 */
@DatabaseTable
public class Customer {
    private long id;

    private String email;

    private Address address;

    private String created_at;

    private boolean active;

    private String signup_ip;

    private String last_login_at;

    private boolean marketing_mails;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSignup_ip() {
        return signup_ip;
    }

    public void setSignup_ip(String signup_ip) {
        this.signup_ip = signup_ip;
    }

    public String getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(String last_login_at) {
        this.last_login_at = last_login_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isMarketing_mails() {
        return marketing_mails;
    }

    public void setMarketing_mails(boolean marketing_mails) {
        this.marketing_mails = marketing_mails;
    }
}
