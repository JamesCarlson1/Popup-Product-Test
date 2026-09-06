package com.inventory.service;

import com.inventory.data.InventoryRepository;
import com.inventory.data.SqliteInventoryRepository;
import com.inventory.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryServiceTest {

    private InventoryService service;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        InventoryRepository repository = new SqliteInventoryRepository(tempDir.resolve("test.db").toString());
        service = new InventoryService(repository);
    }

    @Test
    void createsAndListsInventoriesAlphabetically() {
        service.createInventory("Warehouse");
        service.createInventory("Store");
        assertEquals(List.of("Store", "Warehouse"), service.listInventories());
    }

    @Test
    void rejectsDuplicateInventoryNames() {
        service.createInventory("Warehouse");
        assertThrows(InventoryException.class, () -> service.createInventory("Warehouse"));
    }

    @Test
    void rejectsBlankInventoryName() {
        assertThrows(InventoryException.class, () -> service.createInventory("   "));
    }

    @Test
    void addsAndListsProducts() {
        service.createInventory("Warehouse");
        service.addProduct("Warehouse", "Fridge", 5);
        List<Product> products = service.listProducts("Warehouse");
        assertEquals(1, products.size());
        assertEquals("Fridge", products.get(0).getName());
        assertEquals(5, products.get(0).getQuantity());
    }

    @Test
    void rejectsNegativeStartingQuantity() {
        service.createInventory("Warehouse");
        assertThrows(InventoryException.class, () -> service.addProduct("Warehouse", "Fridge", -1));
    }

    @Test
    void cannotAddProductToMissingInventory() {
        assertThrows(InventoryException.class, () -> service.addProduct("Ghost", "Fridge", 1));
    }

    @Test
    void receiveIncreasesQuantityAndPersists() {
        service.createInventory("Warehouse");
        Product product = service.addProduct("Warehouse", "Fridge", 5);
        service.receive("Warehouse", product, 3);
        assertEquals(8, service.listProducts("Warehouse").get(0).getQuantity());
    }

    @Test
    void sellDecreasesQuantity() {
        service.createInventory("Warehouse");
        Product product = service.addProduct("Warehouse", "Fridge", 5);
        service.sell("Warehouse", product, 2);
        assertEquals(3, product.getQuantity());
    }

    @Test
    void cannotSellMoreThanInStock() {
        service.createInventory("Warehouse");
        Product product = service.addProduct("Warehouse", "Fridge", 5);
        assertThrows(IllegalArgumentException.class, () -> service.sell("Warehouse", product, 10));
    }

    @Test
    void removingInventoryRemovesItsProducts() {
        service.createInventory("Warehouse");
        service.addProduct("Warehouse", "Fridge", 5);
        service.deleteInventory("Warehouse");
        assertThrows(InventoryException.class, () -> service.listProducts("Warehouse"));
    }

    @Test
    void removeProductDropsItFromTheInventory() {
        service.createInventory("Warehouse");
        Product product = service.addProduct("Warehouse", "Fridge", 5);
        service.removeProduct("Warehouse", product);
        assertEquals(List.of(), service.listProducts("Warehouse"));
    }
}
