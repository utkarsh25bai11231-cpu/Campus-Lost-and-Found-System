# Testing and verification

This file records final audit checks actually performed in the current environment. It is not a substitute for testing against a configured MySQL server.

## Environment facts

- Available compiler/runtime: JDK 25.0.3.
- Java 21 compatibility check: compilation used `javac --release 21`.
- Maven was not installed on PATH, so `mvn clean compile` could not be run here.
- No JDK 21 runtime was installed, so execution on an actual Java 21 VM was not possible here.
- MySQL was not available; CLI checks used the documented in-memory fallback.

## Final audit checks

| Check | Result |
| --- | --- |
| Compile all modular sources with Java 21 API target | PASS - `javac --release 21 -cp "lib/*" -d bin ...` completed successfully. |
| Start CLI and verify guest menu | PASS - application banner and main menu displayed. |
| Valid seeded-user login and logout | PASS - Rahul Sharma signed in and out successfully. |
| Public search and rule-based match display | PASS - the seeded bag scored 100/100 and ID card scored 90/100 with factor explanations. |
| Invalid menu input recovery | PASS - `abc` produced a validation message and the menu continued. |
| Report lost/found, matching, and export | PASS - reports #5 and #6 matched at 95/100; export produced six items and zero claims. |
| Claim submission and staff approval | PASS - Rahul submitted Claim #1 for found item #2 and Staff user Ramesh approved it; the item became `CLAIMED`. |
| Database-unavailable fallback | PASS - one MySQL notice was shown and the CLI continued using seeded in-memory data. |
| Persistence after restart with MySQL | Not performed: requires a configured MySQL server. |

## Recommended local verification

With JDK 21 and MySQL configured, run `mvn clean compile`, then `mvn exec:java`. Exercise registration, duplicate registration, login failure, lost/found reports, keyword and multi-filter search, matching, valid/invalid claims, export, logout, restart, and persistence. Use a separate database/schema for repeatable testing.
