package com.example.coffee_shop_app.models;

public class AddressDelivery {
    private  Address address;
    private String nameReceiver;
    private String phone;

    public AddressDelivery(Address address, String nameReceiver, String phone) {
        this.address = address;
        this.nameReceiver = nameReceiver;
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public String getPhone() {
        return phone;
    }
}
