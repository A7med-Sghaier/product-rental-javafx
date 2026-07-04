# Profile and CV Snippets

Use these snippets when referencing Produkt-Ausleihe from GitHub, LinkedIn, a CV or interview notes.

## GitHub repository description

Recommended repository description:

> Modern JavaFX rental-management app with layered architecture, SQLite, prepared statements, CI and tests.

Recommended topics:

```text
java
javafx
sqlite
maven
junit
mockito
jacoco
clean-architecture
desktop-app
portfolio-project
```

## Pinning guidance

This is a strong supporting repository, especially for Java, architecture, testing and maintainability signals.

For a senior React/Node.js portfolio, pin it after stronger full-stack web projects. Suggested order:

1. Flagship React/Node.js/PostgreSQL full-stack app.
2. Second full-stack or API-focused project with tests and CI.
3. Data/ML or automation project if available.
4. `product-rental-javafx` as a Java architecture and modernization supporting project.

Do not present this as the primary flagship for React/Node roles. Present it as proof of broader engineering range and modernization judgment.

## CV bullet

> Modernized a university JavaFX rental-management project into a layered Java 17 desktop app with SQLite persistence, prepared statements, service-level validation, automated tests, CI and architecture documentation.

## LinkedIn project summary

Produkt-Ausleihe is a JavaFX and SQLite desktop rental-management app that I modernized from an older university project into a cleaner portfolio-ready codebase. The refactor introduced layered architecture, repository interfaces, prepared-statement JDBC persistence, `BigDecimal` money calculations, service-level validation, JUnit/Mockito tests, CI and architecture documentation.

## Short interview pitch

This began as a small university JavaFX project. I kept the original desktop-app scope, but treated it like a modernization exercise: I separated UI, service, domain and persistence responsibilities, replaced SQL string concatenation with prepared statements, moved validation into services, added tests and CI, and documented the tradeoffs. The point of the project is not that it is huge; it shows how I improve maintainability and safety in an existing codebase.

## Technical talking points

- Preserved the existing SQLite schema where practical for compatibility.
- Introduced repository interfaces so services do not depend on JDBC details.
- Used `PreparedStatement` throughout the JDBC layer to avoid SQL injection and quoted-input bugs.
- Used `BigDecimal` for rental prices and totals instead of floating-point arithmetic.
- Used file-based temporary SQLite databases in integration tests because each repository call opens a new connection.
- Kept dependency injection manual through `AppContext` because a full framework would be overkill for a small desktop app.
- Documented the modernization with architecture notes, decisions and a case study so reviewers can understand the engineering story quickly.

## One-line portfolio card

JavaFX rental-management desktop app modernized from a university project into a layered Java 17 codebase with SQLite, prepared statements, CI, tests and architecture documentation.
