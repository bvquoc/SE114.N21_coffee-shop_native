package com.example.coffee_shop_app.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class OrderFood {
    String id;
    String idFood;
    String image;
    String name;
    int quantity;
    String size;
    String topping;
    String note;
    double unitPrice;

    public OrderFood(String image, String name, int quantity, String size, double unitPrice) {
        this.image = image;
        this.name = name;
        this.quantity = quantity;
        this.size = size;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFood() {
        return idFood;
    }

    public void setIdFood(String idFood) {
        this.idFood = idFood;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTopping() {
        return topping;
    }

    public void setTopping(String topping) {
        this.topping = topping;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public static OrderFood fromSnapshot(DocumentSnapshot json){
        OrderFood orderFood=new OrderFood(
                json.get("image").toString(),
                json.get("name").toString(),
                ((Number) json.get("quantity")).intValue(),
                json.get("size").toString(),
                ((Number) json.get("unitPrice")).doubleValue());
        orderFood.setId(json.getId());
        orderFood.setIdFood(json.get("idFood").toString());
        orderFood.setTopping(json.contains("topping")? json.get("topping").toString() : null);
        orderFood.setNote(json.contains("note")?json.get("note").toString():null);
        return  orderFood;
    }
//    TODO: fix orderFood
    public static OrderFood fromSnapshot(Map<String, Object> json){
        OrderFood orderFood=new OrderFood(
                json.get("image").toString(),
                json.get("name").toString(),
                ((Number) json.get("quantity")).intValue(),
                json.get("size").toString(),
                ((Number) json.get("unitPrice")).doubleValue());
        orderFood.setIdFood(json.get("id").toString());
        orderFood.setTopping(json.containsKey("topping")? json.get("topping").toString() : null);
        orderFood.setNote(json.containsKey("note")?json.get("note").toString():null);
        return  orderFood;
    }
}
