package com.inventory.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void receiveAddsToQuantity() {
        Product product = new Product("Fridge", 5);
        product.receive(3);
        assertEquals(8, product.getQuantity());
    }

    @Test
    void sellSubtractsFromQuantity() {
        Product product = new Product("Fridge", 5);
        product.sell(2);
        assertEquals(3, product.getQuantity());
    }

    @Test
    void cannotSellMoreThanAvailable() {
        Product product = new Product("Fridge", 5);
        assertThrows(IllegalArgumentException.class, () -> product.sell(6));
    }

    @Test
    void cannotReceiveNegativeAmount() {
        Product product = new Product("Fridge", 5);
        assertThrows(IllegalArgumentException.class, () -> product.receive(-1));
    }

    @Test
    void cannotSellNegativeAmount() {
        Product product = new Product("Fridge", 5);
        assertThrows(IllegalArgumentException.class, () -> product.sell(-1));
    }
}
