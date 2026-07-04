# Produkt-Ausleihe

Produkt-Ausleihe is a small JavaFX desktop application for managing product rentals. It was created as a personal university TD project and has been cleaned up as a portfolio-safe archive of the original work.

Public repository name: `product-rental-javafx`.

## Features

- Manage customers, product categories and products
- Create rental records with start and end dates
- Track rented and returned products
- Filter rental, product and customer tables
- Store data locally in SQLite

## Tech Stack

- Java
- JavaFX
- SQLite
- Maven

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+

### Run the application

```bash
mvn javafx:run
```

The application creates a local `Laiheus.db` SQLite database in the project root when it starts. This runtime database is intentionally ignored by Git so local data is not published.

### Build

```bash
mvn clean package
```

## Project Structure

```text
src/
  buttonHandlers/   UI action helpers
  client/           Customer model
  db/               SQLite access and schema initialization
  gui/              JavaFX screens
  product/          Product, category and rental models
  main/             JavaFX application entry point
```

## Portfolio Context

This repository represents an early Java/SQLite university project. It is useful as a supporting portfolio project because it demonstrates desktop UI development, CRUD workflows, local persistence and basic rental-domain modeling.

For senior full-stack positioning, it should be presented as an archived/cleaned university project rather than a current production-quality system.

## Notes

- The original archive included compiled `.class` files and bundled SQLite JDBC jars. These were removed from the cleaned repository because Maven now resolves dependencies.
- The original sample `Laiheus.db` file contained dummy data and was removed to avoid publishing local runtime data.
- The SQL layer has been updated to use prepared statements for user-provided values.
