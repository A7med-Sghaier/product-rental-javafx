# Portfolio Audit

## Project summary

Produkt-Ausleihe is a small JavaFX desktop application for local product-rental management. It supports customer, category and product management, rental creation, product returns and simple table filtering.

## Tech stack

- Java
- JavaFX
- SQLite
- Maven

## Current strengths

- Complete CRUD-style desktop workflow for a clear business domain
- Local SQLite persistence
- Multiple UI views for customers, products, categories, rentals and returns
- Useful example of early Java and desktop application experience

## Current weaknesses

- Original code uses direct SQL string concatenation instead of prepared statements
- No automated tests
- Original archive included generated build output and duplicated dependency jars
- UI and persistence are tightly coupled, which limits maintainability
- Project structure is older Eclipse-style Java rather than standard Maven layout

## Security or publishing risks

- No secrets were found in the source scan.
- The original SQLite database contained dummy local sample data and was removed from the cleaned repository.
- Public release is reasonable only as a personal university TD project, not as company/client work.

## Portfolio-worthiness assessment

This is portfolio-worthy as a supporting archived university project. It should not be positioned as a flagship senior project, but it can show early Java, JavaFX, SQLite and CRUD application experience.

## Improve existing project vs create sanitized portfolio version

Improve the existing project enough for a clean public archive. A full rewrite is not necessary unless the goal is to create a modern JavaFX/Spring/REST portfolio showcase.

## Recommended repository name

`product-rental-javafx`

## Recommended public/private status

Public is acceptable after the user confirms ownership and publication safety. The README should clearly label it as a personal university TD project.

## Portfolio positioning sentence

JavaFX and SQLite desktop application for managing product rentals, created as a university TD project and cleaned for portfolio review.

## Step-by-step improvement plan

1. Remove generated binaries, bundled jars and local runtime database files.
2. Add Maven build metadata and dependency management.
3. Add `.gitignore` for build outputs, IDE files and local SQLite data.
4. Add a recruiter-readable README with setup, features and portfolio context.
5. Add a basic CI workflow for Maven builds.
6. Future pass: replace SQL string concatenation with prepared statements.
7. Future pass: add small persistence/service tests around database operations.

## Estimated effort

- Cleanup for safe publication: 1 to 2 hours
- Modernization pass with prepared statements and tests: 4 to 8 hours
- Full portfolio rewrite or architecture upgrade: 1 to 2 days
