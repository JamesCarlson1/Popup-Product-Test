package com.inventory.data;

import com.inventory.model.Product;

import java.util.List;

/**
 * Persistence boundary for inventories and their products. Implementations decide
 * how (and where) data is stored; callers should only depend on this interface.
 */
public interface InventoryRepository {

    List<String> listInventoryNames();

    boolean inventoryExists(String name);

    void createInventory(String name);

    void deleteInventory(String name);

    List<Product> listProducts(String inventoryName);

    Product addProduct(String inventoryName, String productName, int quantity);

    void updateProduct(String inventoryName, Product product);

    void removeProduct(String inventoryName, long productId);
}
