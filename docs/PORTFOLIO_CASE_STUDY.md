# Portfolio Case Study: Produkt-Ausleihe

## Snapshot

Produkt-Ausleihe is a JavaFX and SQLite desktop application for managing product rentals: customers, product catalogue, categories, rentals, returns and invoice-style summaries.

The project started as a small university TD exercise and was later modernized into a portfolio-ready Java application with a layered architecture, prepared-statement persistence, automated tests, CI and a refreshed UI.

## Why this project matters

This project is useful in a senior engineering portfolio because it shows how an older, tightly coupled student project can be turned into a maintainable codebase without hiding its origin. The value is not the size of the app; the value is the engineering judgment applied to improve structure, safety, testing and documentation.

## Original state

The original version was a working JavaFX desktop app, but it reflected typical early-project tradeoffs:

- UI classes talked directly to a global database helper.
- SQL was built through string concatenation.
- Monetary values used floating-point numbers.
- Status values were represented as raw strings.
- Build output, local database files and dependency jars were mixed with source code.
- There were no automated tests or CI pipeline.

## Modernization work

The repository was cleaned and reworked around practical maintainability goals:

- Removed generated artifacts, bundled jars and local SQLite data from the repository.
- Added Maven dependency management and GitHub Actions CI.
- Reorganized the application into `domain`, `persistence`, `service`, `ui` and `config` layers.
- Introduced repository interfaces and JDBC implementations using `PreparedStatement`.
- Moved business validation into service classes.
- Replaced `float` money calculations with `BigDecimal`.
- Replaced status strings with a `RentalStatus` enum while preserving database compatibility.
- Added JUnit 5, Mockito and SQLite integration tests.
- Added a modern JavaFX theme, screenshots, architecture documentation and demo-data tooling.

## Architecture highlights

The application follows a simple layered design:

```text
ui -> service -> persistence interfaces -> domain
              -> persistence.jdbc implementations
```

`AppContext` acts as a lightweight composition root. This keeps object creation explicit and avoids adding a framework where one is not needed.

The persistence layer keeps SQLite details behind repository interfaces. Services and UI code work with domain objects and repository contracts, which makes the core behavior easier to test.

## Quality signals

Recruiter-visible quality improvements include:

- CI running `mvn clean verify` on every push and pull request.
- Repository integration tests using temporary file-based SQLite databases.
- Service tests using Mockito for validation and orchestration behavior.
- JaCoCo coverage report generation.
- Architecture documentation explaining design decisions and tradeoffs.
- Screenshots and sample-data tooling for quick project review.

## Tradeoffs

The app intentionally remains a JavaFX desktop application rather than being rewritten as a web app. That keeps the portfolio story honest: the goal was to modernize and preserve a university desktop project, not pretend it was originally a production full-stack platform.

The code uses manual dependency injection through `AppContext` instead of Spring. For this project size, that keeps the architecture explicit and easy to follow.

## What I would improve next

If this project were continued, the next useful improvements would be:

1. Add more end-to-end UI smoke tests around the main rental flow.
2. Improve accessibility and keyboard navigation in the JavaFX views.
3. Add release packaging for a runnable desktop distribution.
4. Expand error handling around database failures and invalid local files.
5. Add a short demo video or animated GIF for the README.

## Resume / interview positioning

Suggested positioning sentence:

> Modernized a university JavaFX rental-management application into a layered Java 17 desktop app with SQLite persistence, prepared statements, service-level validation, automated tests, CI and architecture documentation.

Good talking points:

- How the original code mixed UI and persistence, and how layering improved maintainability.
- Why prepared statements and `BigDecimal` were important upgrades.
- Why file-based temporary SQLite databases are better than `:memory:` for these repository tests.
- How the project keeps backward-compatible table names while improving the Java model.
- Why manual dependency injection was a better fit than adding a full framework.
