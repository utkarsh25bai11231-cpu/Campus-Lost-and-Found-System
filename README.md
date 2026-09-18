# Campus Lost and Found System

A command-line Java application for the CSE2006 Programming in Java evaluated project. It records lost and found items, helps campus users locate possible matches, and supports a controlled claim workflow.

## Problem, users, and scope

Campus notices are often scattered across informal groups and security desks. This system gives Students, Faculty, and Staff one CLI to register or sign in, report items, search records, review rule-based matches, submit/review claims, and export records. It is intentionally a CLI application: it has no GUI, web server, or cloud dependency.

## Features

- Student, Faculty, and Staff accounts with role-specific identifiers.
- Lost and found reports, owner-only edit/cancel/delete operations, and keyword or multi-criteria search.
- Explainable rule-based matching: category (25), location (25), date proximity (20), and description keywords (30), for a maximum of 100.
- Claims with ownership proof; only the finder or a Staff user may approve a pending claim.
- Background matching via `Runnable` and synchronized notification storage.
- MySQL/JDBC persistence when configured, plus an in-memory fallback when MySQL is unavailable.
- Plain-text export through Java file I/O.

## Requirements

- **JDK 21** (the project is compiled with Java 21 APIs using `--release 21`).
- Apache Maven 3.8+ for the Maven commands below, or the bundled MySQL Connector/J JAR for the Windows scripts.
- MySQL 8+ only when persistent storage is required.

## Database setup

1. Create the schema with `schema.sql` in MySQL.
2. Supply credentials without committing a password, either in an untracked local copy of `db.properties` / `src/main/resources/db.properties`, or via environment variables:

```powershell
$env:MYSQL_USER = "your_mysql_user"
$env:MYSQL_PASSWORD = "your_mysql_password"
```

`db.properties` deliberately contains blank credentials. If MySQL is unreachable, the application reports this once and starts with seeded in-memory demo data. That fallback is not persistent across restarts.

## Build and run

From the repository root, with JDK 21 selected:

```powershell
mvn clean compile
mvn exec:java
```

On Windows without Maven, use:

```powershell
.\compile.ps1
.\run.ps1
```

The scripts compile with `javac --release 21`. The equivalent direct commands are:

```powershell
javac --release 21 -cp "lib/*" -d bin (Get-ChildItem -Recurse -Filter "*.java" src/main/java).FullName
Copy-Item "db.properties" "bin\" -Force
java -cp "bin;lib/*" campus.lostfound.Main
```

To build the executable JAR:

```powershell
mvn clean package
java -jar target/campus-lost-and-found-1.0.0.jar
```

## Test accounts in fallback mode

- Student: `rahul@vitbhopal.ac.in` / `pass123`
- Faculty: `ananya@vitbhopal.ac.in` / `prof123`
- Staff: `ramesh@vitbhopal.ac.in` / `staff123`

See [TESTING.md](TESTING.md) for audit results and repeatable checks.

## Project structure

```text
src/main/java/campus/lostfound/
  Main.java                 CLI
  model/                    abstract User and Item hierarchies; reports and claims
  enums/                    user, item, and claim states
  service/                  validation, matching, and claim rules
  repository/               JDBC access and fallback collections
  database/                 connection configuration
  exception/                custom checked exceptions
  util/                     dates, export, and background notifications
schema.sql                  MySQL schema
docs/                       architecture, workflow, UML, ER, use-case, and sequence diagrams
```

## Java concepts demonstrated

Classes, constructors, encapsulation, abstract classes, inheritance, overriding and runtime polymorphism, enums, `ArrayList`, `HashMap`, custom exceptions, packages, JDBC prepared statements, file I/O, `Runnable`, and synchronized shared-state access.

## Non-functional considerations

- Usability: validated, range-checked menu prompts and readable CLI feedback.
- Reliability: exception handling and a non-crashing MySQL fallback.
- Maintainability: separated CLI, service, repository, model, and utility packages.
- Security: parameterized JDBC statements, input validation, ownership checks, and no real database password committed.

## Troubleshooting

- If Maven is not on PATH, install Maven or use the supplied Windows scripts.
- If Java is not version 21, select/install JDK 21 before submitting or evaluating.
- For records to persist between launches, configure MySQL and execute `schema.sql`; the fallback is intended for demonstration only.
