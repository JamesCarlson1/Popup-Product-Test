package com.inventory.data;

import com.inventory.model.Product;
import com.inventory.service.InventoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite-backed implementation of {@link InventoryRepository}. Opens a short-lived
 * connection per operation, which is simple and plenty fast for a desktop-scale
 * inventory tool; a connection pool would be premature for this project's size.
 */
public class SqliteInventoryRepository implements InventoryRepository {

    private final String jdbcUrl;

    public SqliteInventoryRepository(String databaseFile) {
        this.jdbcUrl = "jdbc:sqlite:" + databaseFile;
        initSchema();
    }

    private Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
            return connection;
        } catch (SQLException e) {
            throw new InventoryException("Could not connect to the database: " + e.getMessage(), e);
        }
    }

    private void initSchema() {
        String inventories = "CREATE TABLE IF NOT EXISTS inventories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE)";
        String products = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "inventory_id INTEGER NOT NULL REFERENCES inventories(id) ON DELETE CASCADE," +
                "name TEXT NOT NULL," +
                "quantity INTEGER NOT NULL)";
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute(inventories);
            statement.execute(products);
        } catch (SQLException e) {
            throw new InventoryException("Could not initialize the database schema: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> listInventoryNames() {
        String sql = "SELECT name FROM inventories ORDER BY name";
        List<String> names = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {
            while (rows.next()) {
                names.add(rows.getString("name"));
            }
            return names;
        } catch (SQLException e) {
            throw new InventoryException("Could not list inventories: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean inventoryExists(String name) {
        String sql = "SELECT 1 FROM inventories WHERE name = ?";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException e) {
            throw new InventoryException("Could not check inventory '" + name + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void createInventory(String name) {
        String sql = "INSERT INTO inventories (name) VALUES (?)";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new InventoryException("Could not create inventory '" + name + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteInventory(String name) {
        String sql = "DELETE FROM inventories WHERE name = ?";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new InventoryException("Could not delete inventory '" + name + "': " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> listProducts(String inventoryName) {
        String sql = "SELECT p.id, p.name, p.quantity FROM products p " +
                "JOIN inventories i ON p.inventory_id = i.id " +
                "WHERE i.name = ? ORDER BY p.id";
        List<Product> products = new ArrayList<>();
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryName);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Product product = new Product(rows.getString("name"), rows.getInt("quantity"));
                    product.setId(rows.getLong("id"));
                    products.add(product);
                }
            }
            return products;
        } catch (SQLException e) {
            throw new InventoryException("Could not list products for '" + inventoryName + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Product addProduct(String inventoryName, String productName, int quantity) {
        String findInventory = "SELECT id FROM inventories WHERE name = ?";
        String insert = "INSERT INTO products (inventory_id, name, quantity) VALUES (?, ?, ?)";
        try (Connection connection = connect()) {
            long inventoryId;
            try (PreparedStatement statement = connection.prepareStatement(findInventory)) {
                statement.setString(1, inventoryName);
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next()) {
                        throw new InventoryException("Inventory '" + inventoryName + "' does not exist");
                    }
                    inventoryId = rows.getLong("id");
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, inventoryId);
                statement.setString(2, productName);
                statement.setInt(3, quantity);
                statement.executeUpdate();
                Product product = new Product(productName, quantity);
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setId(keys.getLong(1));
                    }
                }
                return product;
            }
        } catch (SQLException e) {
            throw new InventoryException("Could not add product '" + productName + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void updateProduct(String inventoryName, Product product) {
        String sql = "UPDATE products SET name = ?, quantity = ? WHERE id = ?";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getQuantity());
            statement.setLong(3, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new InventoryException("Could not update product '" + product.getName() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void removeProduct(String inventoryName, long productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new InventoryException("Could not remove product: " + e.getMessage(), e);
        }
    }
}
