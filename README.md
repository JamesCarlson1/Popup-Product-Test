# Inventory Manager

A small inventory management tool with both a command-line interface and a
Swing desktop GUI, backed by a SQLite database. Originally built for a CM111
class assignment as a set of hand-rolled flat-text-file parsers; rewritten
here as a proper layered Java application.

## Features

- Create and delete inventories
- Add, rename, remove, and restock (`receive`/`sell`) products within an
  inventory
- Two interchangeable front ends (CLI and Swing GUI) sharing one service layer
  and one SQLite-backed data store, so both always see the same data
- Input validation (blank names, negative quantities, overselling stock) is
  centralized in the service layer instead of duplicated per UI
- Errors are reported and recovered from inline; the program never crashes on
  bad input

## Architecture

```
com.inventory.model    Product — the domain object
com.inventory.data     InventoryRepository interface + SqliteInventoryRepository (JDBC)
com.inventory.service  InventoryService — validation and business rules
com.inventory.cli      ConsoleApp — command-line front end
com.inventory.ui       InventoryWindow — Swing front end
```

The CLI and GUI are both thin: they collect input, call `InventoryService`,
and render the result. All persistence goes through the `InventoryRepository`
interface, so the storage backend could be swapped without touching either
front end.

## Requirements

- Java 17+
- Maven 3.6+

## Building and testing

```bash
mvn test      # run the unit test suite
mvn package   # build target/inventory-manager.jar
```

## Running

Command-line interface:

```bash
mvn compile exec:java -Dexec.mainClass=com.inventory.cli.ConsoleApp
# or, after `mvn package`:
java -cp target/inventory-manager.jar com.inventory.cli.ConsoleApp
```

Swing GUI:

```bash
mvn compile exec:java -Dexec.mainClass=com.inventory.ui.InventoryWindow
# or, after `mvn package`:
java -cp target/inventory-manager.jar com.inventory.ui.InventoryWindow
```

Both accept an optional argument for the SQLite database file path (default:
`inventory.db` in the working directory), and both read/write the same file.

## Tests

`src/test/java` covers the domain rules (`Product`) and the service layer
(`InventoryService`), including duplicate/blank/negative input, overselling
stock, and cascading deletes, using a temporary SQLite database per test.
