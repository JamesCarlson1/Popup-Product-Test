package com.inventory.model;

/**
 * A single line item tracked inside an inventory: a name and a quantity on hand.
 */
public class Product {

    private long id = -1;
    private String name;
    private int quantity;

    public Product(String name, int quantity) {
        this.name = name;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void receive(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to receive must not be negative");
        }
        quantity += amount;
    }

    public void sell(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to sell must not be negative");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Cannot sell " + amount + " when only " + quantity + " are in stock");
        }
        quantity -= amount;
    }
}
