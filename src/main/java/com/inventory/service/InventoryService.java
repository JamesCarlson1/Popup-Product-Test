package com.inventory.service;

import com.inventory.data.InventoryRepository;
import com.inventory.model.Product;

import java.util.List;

/**
 * Business rules for managing inventories and products, sitting between the
 * persistence layer and any front end (CLI or GUI). Both front ends validate
 * user input by calling through here instead of duplicating the checks.
 */
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public List<String> listInventories() {
        return repository.listInventoryNames();
    }

    public void createInventory(String name) {
        String trimmed = requireNonBlank(name, "Inventory name");
        if (repository.inventoryExists(trimmed)) {
            throw new InventoryException("An inventory named '" + trimmed + "' already exists");
        }
        repository.createInventory(trimmed);
    }

    public void deleteInventory(String name) {
        requireInventoryExists(name);
        repository.deleteInventory(name);
    }

    public List<Product> listProducts(String inventoryName) {
        requireInventoryExists(inventoryName);
        return repository.listProducts(inventoryName);
    }

    public Product addProduct(String inventoryName, String productName, int quantity) {
        requireInventoryExists(inventoryName);
        String trimmedName = requireNonBlank(productName, "Product name");
        requireNonNegative(quantity, "Quantity");
        return repository.addProduct(inventoryName, trimmedName, quantity);
    }

    public void renameProduct(String inventoryName, Product product, String newName) {
        product.setName(requireNonBlank(newName, "Product name"));
        repository.updateProduct(inventoryName, product);
    }

    public void setQuantity(String inventoryName, Product product, int quantity) {
        requireNonNegative(quantity, "Quantity");
        product.setQuantity(quantity);
        repository.updateProduct(inventoryName, product);
    }

    public void receive(String inventoryName, Product product, int amount) {
        product.receive(amount);
        repository.updateProduct(inventoryName, product);
    }

    public void sell(String inventoryName, Product product, int amount) {
        product.sell(amount);
        repository.updateProduct(inventoryName, product);
    }

    public void removeProduct(String inventoryName, Product product) {
        repository.removeProduct(inventoryName, product.getId());
    }

    private void requireInventoryExists(String name) {
        if (name == null || !repository.inventoryExists(name)) {
            throw new InventoryException("Inventory '" + name + "' does not exist");
        }
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InventoryException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new InventoryException(fieldName + " must not be negative");
        }
    }
}
