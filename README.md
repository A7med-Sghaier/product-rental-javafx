<div align="center">

<img src="./assets/banner.svg" width="100%" alt="Produkt-Ausleihe" />

<br/>

<a href="https://github.com/A7med-Sghaier/product-rental-javafx">
  <img src="https://readme-typing-svg.demolab.com?font=Segoe+UI&weight=600&size=20&pause=1000&color=2DD4BF&center=true&vCenter=true&width=760&lines=JavaFX+rental-management+desktop+app;Layered+architecture+%C2%B7+dependencies+inward;SQLite+%C2%B7+prepared+statements+%C2%B7+no+SQL+injection;JUnit+5+%C2%B7+Mockito+%C2%B7+JaCoCo+%C2%B7+CI" alt="Desktop app for managing product rentals, backed by SQLite." />
</a>

<br/><br/>

[![CI](https://github.com/A7med-Sghaier/product-rental-javafx/actions/workflows/ci.yml/badge.svg)](https://github.com/A7med-Sghaier/product-rental-javafx/actions/workflows/ci.yml)
[![License: EPL 2.0](https://img.shields.io/badge/License-EPL_2.0-2C2255?style=for-the-badge)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-1E90FF?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjfx.io)
[![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org)

![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=flat-square&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=flat-square)
![JaCoCo](https://img.shields.io/badge/JaCoCo-coverage-D22128?style=flat-square)

</div>

A modern **JavaFX** desktop application for managing product rentals — customers, catalogue,
categories, rentals, returns and invoicing — backed by a local **SQLite** database.

> [!NOTE]
> Originally a university project, **re-architected into a clean, layered, enterprise-style
> codebase**: prepared-statement JDBC repositories (no SQL injection), `BigDecimal` money, a
> `RentalStatus` enum, framework-free dependency injection, a token-based UI theme, and an
> automated test suite.

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Evaluate in 3 minutes

1. Skim the screenshots below to understand the product workflow and UI scope.
2. Review the architecture summary and [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) to see the layered design.
3. Check the test suite under `src/test/java` for domain, service and SQLite repository coverage.
4. Run `mvn test` or inspect the GitHub Actions history to verify the automated checks.
5. Read [`docs/PORTFOLIO_CASE_STUDY.md`](docs/PORTFOLIO_CASE_STUDY.md) for the modernization story and interview talking points.
6. Use [`docs/PROFILE_SNIPPETS.md`](docs/PROFILE_SNIPPETS.md) for the GitHub description, topics, CV bullet and LinkedIn wording.

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Screenshots

### Dashboard — active rentals at a glance

![Dashboard](docs/screenshots/dashboard.png)

### Products — catalogue with live availability

![Products](docs/screenshots/products.png)

### Customers — searchable customer management

![Customers](docs/screenshots/customers.png)

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Features

- **Dashboard** — KPI cards (active rentals, active customers, open revenue) and a
  filterable table of all active rentals (by customer, product and date range).
- **Customers, Products, Categories** — full CRUD via themed modal dialogs, with search
  and, for products, category/availability filters and live status pills.
- **New Rental** — pick a customer, add available products with rental periods, watch the
  total update live, then save and generate an invoice.
- **Returns** — select a customer with active rentals and return individual products.
- **Invoice** — a clean receipt summarising the rental and total.
- Local **SQLite** persistence, created automatically on first run.

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Tech stack

- Java 17, JavaFX 17
- SQLite (via `sqlite-jdbc`), accessed through hand-written JDBC repositories
- Maven
- JUnit 5 + Mockito for testing, JaCoCo for coverage

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Architecture

The application follows a layered architecture with dependencies pointing inward; the UI
depends only on services, and JDBC details never leak upward.

```
ui  ──▶  service  ──▶  persistence (repository interfaces)  ──▶  domain
                                    ▲
                          persistence.jdbc (SQLite implementations)

config.AppContext  = composition root that wires everything together
```

| Layer          | Responsibility                                                        |
| -------------- | --------------------------------------------------------------------- |
| `domain`       | Pure POJOs (`Customer`, `Product`, `Category`, `Rental`), the `RentalStatus` enum and exceptions. No JavaFX, no JDBC. |
| `persistence`  | Repository **interfaces**, `ConnectionFactory`, `SchemaInitializer`.  |
| `persistence.jdbc` | SQLite implementations using **prepared statements** throughout.  |
| `service`      | Business rules and input validation.                                  |
| `ui`           | JavaFX shell, views, dialogs and the `theme.css` design system.       |
| `config`       | `AppContext` — manual dependency injection (no framework).            |

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full design, data model and the
rationale behind the key decisions.

For the portfolio story behind the modernization, see
[`docs/PORTFOLIO_CASE_STUDY.md`](docs/PORTFOLIO_CASE_STUDY.md). For architecture decision
records, see [`docs/DECISIONS.md`](docs/DECISIONS.md). For GitHub profile, CV and LinkedIn
copy, see [`docs/PROFILE_SNIPPETS.md`](docs/PROFILE_SNIPPETS.md).

### Notable improvements over the original

- **No SQL injection** — every query is parameterized (the original concatenated strings).
- **Separation of concerns** — UI, business logic and persistence are decoupled and each
  independently testable, replacing the original UI-reaches-into-a-global-DB design.
- **`BigDecimal` money** instead of `float`, and a `RentalStatus` enum instead of magic
  strings (persisted values are unchanged, so **existing databases still load**).
- **Automated tests** and a **modern, token-based UI theme**.

### Before / after modernization

| Area | Original project | Modernized repository |
| --- | --- | --- |
| Structure | UI classes directly used a global DB helper | Layered `domain` / `service` / `persistence` / `ui` design |
| Persistence | SQL built through string concatenation | JDBC repositories with `PreparedStatement` |
| Money | `float` calculations | `BigDecimal` for prices and totals |
| Status | Raw strings | `RentalStatus` enum with database-compatible values |
| Dependencies | Bundled jars and generated build files | Maven-managed dependencies |
| Testing | No automated tests | JUnit 5, Mockito and SQLite integration tests |
| CI | No pipeline | GitHub Actions running `mvn clean verify` |
| Documentation | Source dump | README, screenshots, architecture docs, decisions and case study |

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Getting started

### Prerequisites

- JDK 17+
- Maven 3.8+

### Run

```bash
mvn javafx:run
```

The app creates a local `Laiheus.db` SQLite database in the project root on first start and
seeds the default categories. This runtime database is intentionally git-ignored.

### Load demo data (for screenshots / exploration)

To populate the database with realistic sample customers, products and active rentals:

```bash
mvn -q compile exec:java -Dexec.mainClass=com.ahmedsghaier.rental.devtools.SampleDataLoader
```

Then run `mvn javafx:run` to see a fully-populated dashboard. The loader is safe to keep
around: it does nothing if the database already has customers (pass `--force` to add the
demo data anyway). See
[`SampleDataLoader`](src/main/java/com/ahmedsghaier/rental/devtools/SampleDataLoader.java).

The screenshots above are regenerated (off-screen, via JavaFX `snapshot()`) with:

```bash
mvn -q javafx:run -DmainClass=com.ahmedsghaier.rental.devtools.ScreenshotTool
```

### Build

```bash
mvn clean package
```

### Test

```bash
mvn test
```

Runs the JUnit 5 suite (domain, service and repository integration tests). A coverage
report is written to `target/site/jacoco/index.html`.

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Project structure

```text
src/main/java/com/ahmedsghaier/rental/
  domain/            Domain model + RentalStatus enum + exceptions
  persistence/       Repository interfaces, ConnectionFactory, SchemaInitializer
  persistence/jdbc/  JDBC/SQLite repository implementations (prepared statements)
  service/           Application services with validation
  config/            AppContext composition root
  ui/                JavaFX shell, views, dialogs, components
src/main/resources/com/ahmedsghaier/rental/ui/theme.css   Design system
src/test/java/...    JUnit 5 + Mockito tests
```

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

## Portfolio context

This repository demonstrates layered application design, clean persistence with prepared
statements, dependency injection without a framework, a modern JavaFX UI, and a pragmatic
automated test strategy — grown out of an earlier university rental project.

<div align="center"><img src="./assets/divider.svg" width="70%" alt="" /></div>

<div align="center">

### Built by Ahmed Sghaier — Senior Full-Stack Engineer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ahmed-sghaier-449778137)
[![Email](https://img.shields.io/badge/Email-a7mado008@gmail.com-EA4335?style=for-the-badge&logo=gmail&logoColor=white)](mailto:a7mado008@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-A7med--Sghaier-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/A7med-Sghaier)

</div>
