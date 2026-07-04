# Architecture

This document describes the design of Produkt-Ausleihe: its layers, data model and the
reasoning behind the main decisions.

## Overview

The application is a JavaFX desktop client over a local SQLite database, organised as a
**layered architecture**. Dependencies point inward — outer layers depend on inner ones,
never the reverse — which keeps the domain and business logic free of UI and database
concerns and makes them straightforward to test.

```
          ┌─────────────────────────────────────────────┐
          │                     ui                        │   JavaFX views, dialogs, theme
          │        (RentalApplication, MainView …)        │
          └───────────────────────┬─────────────────────┘
                                  │ depends on
          ┌───────────────────────▼─────────────────────┐
          │                  service                      │   business rules + validation
          │   CustomerService, ProductService,            │
          │   CategoryService, RentalService              │
          └───────────────────────┬─────────────────────┘
                                  │ depends on
          ┌───────────────────────▼─────────────────────┐
          │        persistence (interfaces)               │   *Repository, ConnectionFactory
          │  ┌─────────────────────────────────────────┐  │
          │  │   persistence.jdbc (implementations)      │  │   PreparedStatement / SQLite
          │  └─────────────────────────────────────────┘  │
          └───────────────────────┬─────────────────────┘
                                  │ depends on
          ┌───────────────────────▼─────────────────────┐
          │                   domain                      │   Customer, Product, Category,
          │        (pure POJOs, enum, exceptions)         │   Rental, RentalStatus
          └─────────────────────────────────────────────┘

  config.AppContext — composition root: constructs and wires all of the above.
```

## Layers

### `domain`
Plain Java objects with no dependency on JavaFX or JDBC:
- `Customer`, `Product`, `Category`, `Rental` — entities.
- `ProductAvailability` — a read model pairing a product with its current status.
- `RentalStatus` — enum (`AVAILABLE` / `RENTED` / `RETURNED`) whose `dbValue()` preserves
  the exact strings used by the original database.
- `exception/ValidationException` — carries user-facing messages for rule violations.
- `exception/DataAccessException` — wraps low-level `SQLException`s.

Business calculations that belong to an entity live on it — e.g. `Rental.getDays()` and
`Rental.getTotal()`.

### `persistence`
Defines repository **interfaces** (`CustomerRepository`, `CategoryRepository`,
`ProductRepository`, `RentalRepository`) plus:
- `ConnectionFactory` — the single place the JDBC URL lives; enables foreign keys on every
  connection.
- `SchemaInitializer` — idempotent `CREATE TABLE IF NOT EXISTS` + category seeding.

### `persistence.jdbc`
SQLite implementations of the repository interfaces. **Every statement is a
`PreparedStatement` with bound parameters**, eliminating the SQL-injection risk of the
original string-concatenated queries. `SQLException`s are wrapped in `DataAccessException`
so JDBC never leaks into upper layers. Batch inserts (checkout) run inside a transaction.

### `service`
Application services that validate input and orchestrate repositories. The UI only ever
talks to services. Example rules: a customer needs a first and last name; a product price
cannot be negative; a rental's return date must be after its start date; checkout requires
a non-empty basket.

### `ui`
JavaFX presentation layer:
- `RentalApplication` — entry point; builds the `AppContext`, mounts `MainView`, applies
  `theme.css`.
- `MainView` — the shell: a sidebar of toggle-button navigation plus a swappable content
  area. Implements `Navigator` so views can request screen changes without knowing about
  the shell.
- `view/*` — one class per screen (Dashboard, Customers, Products, Categories, NewRental,
  Returns, Invoice).
- `dialog/*` — themed modal forms and pickers for CRUD and selection.
- `component/Widgets` — factory for styled buttons, KPI cards, headings and value
  formatting (German currency/date).
- `Styles` — constants mirroring the CSS class names in `theme.css`.

### `config`
`AppContext` is the composition root — the only place concrete implementations are chosen.
It takes a `ConnectionFactory`, so production uses the on-disk database while tests pass a
throwaway one.

## Data model

SQLite tables (names kept identical to the original for backward compatibility):

| Table        | Columns                                                        |
| ------------ | ------------------------------------------------------------- |
| `clients`    | id, firstname, lastname, address, plz, city, tel              |
| `categories` | id, label (unique)                                            |
| `products`   | id, label, preis, categorie_id → categories(id)               |
| `rents`      | id, c_id → clients(id), p_id → products(id), status, date_from, date_to |

- Money is stored numerically and mapped to `BigDecimal`.
- Dates are stored as ISO-8601 strings (`yyyy-MM-dd`) and mapped to `LocalDate`.
- A product is "available" when it has no `rents` row whose status is not `returned`.

## Key decisions

- **Manual DI over a framework.** The graph is small; `AppContext` keeps startup explicit
  and testable without Spring.
- **Prepared statements everywhere.** Security and correctness; verified by a regression
  test that stores a value containing an apostrophe.
- **Backward-compatible schema.** Table/column names and status strings are unchanged, so a
  database created by the original application still opens.
- **`BigDecimal` for money.** Avoids the rounding errors of the original `float` totals.
- **Pure-POJO domain.** JavaFX `TableView` binds to the standard getters via
  `PropertyValueFactory`/cell value factories, so the domain stays framework-free.

## Testing strategy

- **Domain** — unit tests for calculations and the status enum round-trip.
- **Service** — Mockito-based tests for validation rules and orchestration (no database).
- **Persistence** — integration tests against a fresh **file-based** SQLite database in a
  JUnit `@TempDir` (a new connection is opened per call, so a shared `:memory:` database
  would not survive between calls). These cover CRUD, the availability join, transactional
  batch insert, status transitions, and the anti-injection guarantee.

Run everything with `mvn test`; coverage is reported by JaCoCo at
`target/site/jacoco/index.html`.
