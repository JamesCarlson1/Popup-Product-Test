package com.inventory.cli;

import com.inventory.data.InventoryRepository;
import com.inventory.data.SqliteInventoryRepository;
import com.inventory.model.Product;
import com.inventory.service.InventoryException;
import com.inventory.service.InventoryService;

import java.util.List;
import java.util.Scanner;

/**
 * Command-line front end for the inventory manager. Bad input (a non-numeric
 * choice, a name that doesn't exist) is reported and re-prompted rather than
 * crashing the program.
 */
public class ConsoleApp {

    private final InventoryService service;
    private final Scanner input;

    public ConsoleApp(InventoryService service, Scanner input) {
        this.service = service;
        this.input = input;
    }

    public static void main(String[] args) {
        String databaseFile = args.length > 0 ? args[0] : "inventory.db";
        InventoryRepository repository = new SqliteInventoryRepository(databaseFile);
        InventoryService service = new InventoryService(repository);
        new ConsoleApp(service, new Scanner(System.in)).run();
    }

    public void run() {
        System.out.println("Inventory Manager");
        boolean running = true;
        while (running) {
            try {
                running = mainMenu();
            } catch (InventoryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
        System.out.println("Goodbye.");
    }

    private boolean mainMenu() {
        List<String> inventories = service.listInventories();
        System.out.println();
        if (inventories.isEmpty()) {
            System.out.println("No inventories yet.");
        } else {
            for (int i = 0; i < inventories.size(); i++) {
                System.out.printf("  %d) %s%n", i + 1, inventories.get(i));
            }
        }
        System.out.println("  n) new inventory");
        if (!inventories.isEmpty()) {
            System.out.println("  d) delete an inventory");
        }
        System.out.println("  q) quit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        if (choice.equalsIgnoreCase("q")) {
            return false;
        } else if (choice.equalsIgnoreCase("n")) {
            System.out.print("New inventory name: ");
            service.createInventory(input.nextLine());
        } else if (choice.equalsIgnoreCase("d") && !inventories.isEmpty()) {
            System.out.print("Name of the inventory to delete: ");
            service.deleteInventory(input.nextLine().trim());
        } else {
            int index = Integer.parseInt(choice) - 1;
            if (index < 0 || index >= inventories.size()) {
                throw new InventoryException("No such inventory: " + choice);
            }
            inventoryMenu(inventories.get(index));
        }
        return true;
    }

    private void inventoryMenu(String inventoryName) {
        boolean inInventory = true;
        while (inInventory) {
            try {
                List<Product> products = service.listProducts(inventoryName);
                System.out.println();
                System.out.println("Inventory: " + inventoryName);
                if (products.isEmpty()) {
                    System.out.println("  (no products yet)");
                } else {
                    for (int i = 0; i < products.size(); i++) {
                        Product product = products.get(i);
                        System.out.printf("  %d) %s - %d%n", i + 1, product.getName(), product.getQuantity());
                    }
                }
                System.out.println("  a) add a product");
                System.out.println("  b) back to inventories");
                System.out.print("> ");
                String choice = input.nextLine().trim();

                if (choice.equalsIgnoreCase("b")) {
                    inInventory = false;
                } else if (choice.equalsIgnoreCase("a")) {
                    addProduct(inventoryName);
                } else {
                    int index = Integer.parseInt(choice) - 1;
                    if (index < 0 || index >= products.size()) {
                        throw new InventoryException("No such product: " + choice);
                    }
                    productMenu(inventoryName, products.get(index));
                }
            } catch (InventoryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private void addProduct(String inventoryName) {
        System.out.print("Product name: ");
        String name = input.nextLine();
        System.out.print("Starting quantity: ");
        int quantity = Integer.parseInt(input.nextLine().trim());
        service.addProduct(inventoryName, name, quantity);
    }

    private void productMenu(String inventoryName, Product product) {
        System.out.println();
        System.out.printf("%s - %d%n", product.getName(), product.getQuantity());
        System.out.println("  1) rename");
        System.out.println("  2) set quantity");
        System.out.println("  3) receive stock");
        System.out.println("  4) sell stock");
        System.out.println("  5) remove product");
        System.out.println("  b) back");
        System.out.print("> ");
        String choice = input.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("New name: ");
                service.renameProduct(inventoryName, product, input.nextLine());
            }
            case "2" -> {
                System.out.print("New quantity: ");
                service.setQuantity(inventoryName, product, Integer.parseInt(input.nextLine().trim()));
            }
            case "3" -> {
                System.out.print("Amount received: ");
                service.receive(inventoryName, product, Integer.parseInt(input.nextLine().trim()));
            }
            case "4" -> {
                System.out.print("Amount sold: ");
                service.sell(inventoryName, product, Integer.parseInt(input.nextLine().trim()));
            }
            case "5" -> {
                System.out.print("Type the product name to confirm removal: ");
                if (input.nextLine().trim().equalsIgnoreCase(product.getName())) {
                    service.removeProduct(inventoryName, product);
                    System.out.println("Removed.");
                } else {
                    System.out.println("Confirmation did not match, nothing removed.");
                }
            }
            case "b" -> {
            }
            default -> throw new InventoryException("No such option: " + choice);
        }
    }
}
