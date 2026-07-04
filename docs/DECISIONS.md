# Architecture Decisions

This document captures the main engineering decisions behind the modernization of Produkt-Ausleihe. It is intentionally short and practical: each decision explains the context, choice and tradeoff.

## 1. Keep JavaFX instead of rewriting as a web app

**Context:** The original project was a JavaFX desktop application. A full web rewrite would have produced a more common portfolio shape, but it would also blur the project history.

**Decision:** Keep the project as a JavaFX desktop app and modernize the architecture around it.

**Why:** This preserves the honest university-project origin while still demonstrating maintainability, UI structure, persistence, testing and CI.

**Tradeoff:** The project is less aligned with React/Node.js roles than a web app, so it should be positioned as a supporting Java/architecture project rather than the flagship full-stack repo.

## 2. Use layered architecture

**Context:** The original UI classes were tightly coupled to database operations.

**Decision:** Split the app into `domain`, `persistence`, `persistence.jdbc`, `service`, `ui` and `config` layers.

**Why:** The domain and service behavior become easier to understand and test. JDBC details stay behind repository interfaces, and UI code can focus on presentation.

**Tradeoff:** There are more files and concepts than in the original student version, but the structure pays off by making responsibilities clear.

## 3. Use manual dependency injection instead of Spring

**Context:** The app needs dependency wiring, but it is still a small desktop application.

**Decision:** Use `config.AppContext` as a composition root instead of adding Spring or another framework.

**Why:** Manual dependency injection keeps startup explicit, avoids framework overhead and is easy to inspect in a portfolio review.

**Tradeoff:** Larger applications would benefit from framework-level lifecycle management, but that would be unnecessary weight here.

## 4. Keep SQLite as the persistence store

**Context:** The application is a local desktop rental-management tool, not a multi-user server system.

**Decision:** Keep SQLite and improve the way it is accessed.

**Why:** SQLite fits local desktop usage, makes the app easy to run, and allows integration tests to use temporary file-based databases.

**Tradeoff:** SQLite is not intended for the same concurrent multi-user workloads as PostgreSQL or MySQL. That is acceptable for this app's scope.

## 5. Use repository interfaces with JDBC implementations

**Context:** The app needs data access, but direct JDBC calls from UI or service classes would recreate the original coupling problem.

**Decision:** Define repository interfaces and implement them with SQLite/JDBC classes.

**Why:** Services depend on contracts, not concrete SQL code. Tests can mock repositories at the service layer and use real SQLite at the persistence layer.

**Tradeoff:** Interfaces add some ceremony, but they make the architecture easier to explain and verify.

## 6. Use prepared statements everywhere

**Context:** The original database code concatenated SQL strings, which is unsafe and fragile when values contain quotes.

**Decision:** Use `PreparedStatement` with bound parameters in the JDBC layer.

**Why:** This prevents SQL injection, handles quoted input safely and makes SQL intent clearer.

**Tradeoff:** Prepared statements require slightly more code than string concatenation, but the safety benefit is non-negotiable.

## 7. Use BigDecimal for money

**Context:** Rental totals are money values. The original code used floating-point numbers.

**Decision:** Use `BigDecimal` for price and total calculations.

**Why:** `BigDecimal` avoids floating-point rounding surprises and is the standard Java choice for money-like values.

**Tradeoff:** `BigDecimal` is more verbose than `float` or `double`, but the correctness improvement is worth it.

## 8. Preserve database compatibility where practical

**Context:** The original app used German table and column names such as `clients`, `products`, `categorie_id` and `rents`.

**Decision:** Keep the existing schema names while improving Java-side naming and structure.

**Why:** Existing databases can still be opened, and the modernization remains a refactor rather than a breaking rewrite.

**Tradeoff:** Some database names are not ideal, but preserving compatibility is more valuable for this project story.

## 9. Use file-based SQLite databases in integration tests

**Context:** Repositories open new connections per operation. A plain `:memory:` SQLite database would not persist across those connections.

**Decision:** Use temporary file-based SQLite databases in repository integration tests.

**Why:** Each test gets an isolated database while still exercising realistic connection behavior.

**Tradeoff:** File-based tests are a little slower than pure unit tests, but they give much better confidence in the persistence layer.

## 10. Keep portfolio documentation close to the code

**Context:** Recruiters and reviewers often scan quickly and may not infer the modernization story from code alone.

**Decision:** Include architecture notes, a case study and reviewer-oriented README sections inside the repository.

**Why:** This turns the repo into a self-contained technical artifact that explains both implementation and engineering judgment.

**Tradeoff:** More documentation needs to be kept accurate as the project evolves.
