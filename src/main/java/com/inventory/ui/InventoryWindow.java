package com.inventory.ui;

import com.inventory.data.InventoryRepository;
import com.inventory.data.SqliteInventoryRepository;
import com.inventory.model.Product;
import com.inventory.service.InventoryException;
import com.inventory.service.InventoryService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Swing front end for the inventory manager: a list of inventories on the left,
 * a table of the selected inventory's products on the right. All state lives in
 * the database via {@link InventoryService}; this class only renders it.
 */
public class InventoryWindow extends JFrame {

    private final InventoryService service;
    private final DefaultListModel<String> inventoryListModel = new DefaultListModel<>();
    private final JList<String> inventoryList = new JList<>(inventoryListModel);
    private final ProductTableModel productTableModel = new ProductTableModel();
    private final JTable productTable = new JTable(productTableModel);

    public InventoryWindow(InventoryService service) {
        super("Inventory Manager");
        this.service = service;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(buildInventoryPanel(), BorderLayout.WEST);
        add(buildProductPanel(), BorderLayout.CENTER);
        setSize(800, 500);
        setLocationRelativeTo(null);
        reloadInventories();
    }

    public static void main(String[] args) {
        String databaseFile = args.length > 0 ? args[0] : "inventory.db";
        InventoryRepository repository = new SqliteInventoryRepository(databaseFile);
        InventoryService service = new InventoryService(repository);
        SwingUtilities.invokeLater(() -> new InventoryWindow(service).setVisible(true));
    }

    private JPanel buildInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Inventories"));
        inventoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                reloadProducts();
            }
        });
        panel.add(new JScrollPane(inventoryList), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 4, 4));
        buttons.add(button("New Inventory", e -> onCreateInventory()));
        buttons.add(button("Delete Inventory", e -> onDeleteInventory()));
        panel.add(buttons, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(220, 0));
        return panel;
    }

    private JPanel buildProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Products"));
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 6, 4, 4));
        buttons.add(button("Add", e -> onAddProduct()));
        buttons.add(button("Rename", e -> onRenameProduct()));
        buttons.add(button("Set Qty", e -> onSetQuantity()));
        buttons.add(button("Receive", e -> onReceive()));
        buttons.add(button("Sell", e -> onSell()));
        buttons.add(button("Remove", e -> onRemoveProduct()));
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JButton button(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        return button;
    }

    private void reloadInventories() {
        String previouslySelected = inventoryList.getSelectedValue();
        inventoryListModel.clear();
        for (String name : service.listInventories()) {
            inventoryListModel.addElement(name);
        }
        if (previouslySelected != null && inventoryListModel.contains(previouslySelected)) {
            inventoryList.setSelectedValue(previouslySelected, true);
        } else if (!inventoryListModel.isEmpty()) {
            inventoryList.setSelectedIndex(0);
        } else {
            reloadProducts();
        }
    }

    private void reloadProducts() {
        String selected = inventoryList.getSelectedValue();
        productTableModel.setProducts(selected == null ? List.of() : service.listProducts(selected));
    }

    private String selectedInventory() {
        String selected = inventoryList.getSelectedValue();
        if (selected == null) {
            throw new InventoryException("Select an inventory first");
        }
        return selected;
    }

    private Product selectedProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            throw new InventoryException("Select a product first");
        }
        return productTableModel.getProductAt(row);
    }

    private void onCreateInventory() {
        runAction(() -> {
            String name = JOptionPane.showInputDialog(this, "New inventory name:");
            if (name != null) {
                service.createInventory(name);
                reloadInventories();
            }
        });
    }

    private void onDeleteInventory() {
        runAction(() -> {
            String name = selectedInventory();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete inventory '" + name + "' and all its products?",
                    "Confirm delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.deleteInventory(name);
                reloadInventories();
            }
        });
    }

    private void onAddProduct() {
        runAction(() -> {
            String inventoryName = selectedInventory();
            String name = JOptionPane.showInputDialog(this, "Product name:");
            if (name == null) {
                return;
            }
            String quantityText = JOptionPane.showInputDialog(this, "Starting quantity:");
            if (quantityText == null) {
                return;
            }
            service.addProduct(inventoryName, name, Integer.parseInt(quantityText.trim()));
            reloadProducts();
        });
    }

    private void onRenameProduct() {
        runAction(() -> {
            String inventoryName = selectedInventory();
            Product product = selectedProduct();
            String name = JOptionPane.showInputDialog(this, "New name:", product.getName());
            if (name != null) {
                service.renameProduct(inventoryName, product, name);
                reloadProducts();
            }
        });
    }

    private void onSetQuantity() {
        runAction(() -> {
            String inventoryName = selectedInventory();
            Product product = selectedProduct();
            String text = JOptionPane.showInputDialog(this, "New quantity:", product.getQuantity());
            if (text != null) {
                service.setQuantity(inventoryName, product, Integer.parseInt(text.trim()));
                reloadProducts();
            }
        });
    }

    private void onReceive() {
        runAction(() -> {
            String inventoryName = selectedInventory();
            Product product = selectedProduct();
            String text = JOptionPane.showInputDialog(this, "Amount received:");
            if (text != null) {
                service.receive(inventoryName, product, Integer.parseInt(text.trim()));
                reloadProducts();
            }
        });
    }

    private void onSell() {
        runAction(() -> {
            String inventoryName = selectedInventory();
            Product product = selectedProduct();
            String text = JOptionPane.showInputDialog(this, "Amount sold:");
            if (text != null) {
                service.sell(inventoryName, product, Integer.parseInt(text.trim()));
                reloadProducts();
            }
        });
    }

    private void onRemoveProduct() {
        runAction(() -> {
            String inventoryName = selectedInventory();
            Product product = selectedProduct();
            int confirm = JOptionPane.showConfirmDialog(this, "Remove '" + product.getName() + "'?",
                    "Confirm remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.removeProduct(inventoryName, product);
                reloadProducts();
            }
        });
    }

    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (InventoryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ProductTableModel extends AbstractTableModel {
        private final String[] columns = {"Name", "Quantity"};
        private List<Product> products = List.of();

        void setProducts(List<Product> products) {
            this.products = products;
            fireTableDataChanged();
        }

        Product getProductAt(int row) {
            return products.get(row);
        }

        @Override
        public int getRowCount() {
            return products.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product product = products.get(rowIndex);
            return columnIndex == 0 ? product.getName() : product.getQuantity();
        }
    }
}
