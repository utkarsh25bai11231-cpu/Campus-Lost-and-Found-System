# Project Statement: Campus Lost and Found System

**Course:** CSE2006 – Programming in Java  
**Institution:** VIT Bhopal University  
**Project Category:** VITyarthi Build Your Own Project (Academic CLI System)  

---

## 1. Problem Statement

Across higher education campuses such as VIT Bhopal, students, faculty, and administrative staff frequently misplace personal belongings—including student identity cards, scientific calculators, laptops, chargers, keys, water bottles, and textbooks. 

Currently, campuses rely on fragmented, informal methods to locate misplaced belongings:
- Unorganized messaging groups (WhatsApp/Telegram) where notices get buried rapidly.
- Physical inquiry desks at distant academic blocks or security gates.
- Lack of a centralized repository to correlate lost reports with items deposited at security or administrative desks.
- Absence of an ownership verification protocol, making handovers unreliable and hard to audit.

This results in items remaining unclaimed in security offices for months, unnecessary financial strain on students replacing lost items, and administrative confusion.

---

## 2. Project Scope

The **Campus Lost & Found System** is a lightweight, pure command-line interface (CLI) software system developed in Java. It provides a centralized, authenticated platform to record, manage, match, and resolve lost and found items within a university campus.

The scope encompasses:
1. **Role-Based Campus User Management**: Distinct profiles for Students, Faculty, and Administrative Staff with specific identification attributes.
2. **Standardized Incident Reporting**: Structured reporting for both lost items (with rewards and identifying marks) and found items (with secure deposit locations).
3. **Multi-Factor Search & Filtering**: Fast item retrieval using combinations of keywords, categories, dates, locations, and item statuses.
4. **Automated Rule-Based Match Engine**: Transparent scoring algorithm (0–100) evaluating category, location, date proximity, and descriptive keyword similarity to detect possible matches without requiring complex machine learning.
5. **Ownership Claim Lifecycle**: Formal claim requests requiring verifiable proof of ownership, complete with review, approval, and item status resolution.
6. **Background Concurrency & Persistence**: Asynchronous matching notifications using Java multithreading, thread-safe alert dispatch, file export utilities, and relational database persistence via MySQL and JDBC (with an automatic in-memory fallback for standalone evaluations).

Out of Scope:
- Graphical User Interfaces (GUI/JavaFX/Swing) or Web APIs (Spring Boot/React), ensuring evaluation can be performed purely from standard university lab terminals.

---

## 3. Target Users

| User Persona | Typical Campus Scenario | Key Operations |
| :--- | :--- | :--- |
| **Students** | Misplaced ID card in food court or calculator in exam hall. | Report lost belongings, search found registries, claim found items with proof, check match scores. |
| **Faculty** | Misplaced syllabus books or found unattended pen drives in classrooms. | Report found items, submit claims for office belongings, review reports. |
| **Staff & Campus Security** | Custodians of lost items handed over at main gates or proctor offices. | Log found articles with deposit locations, verify owner claims, approve or reject claims, export audit reports. |

---

## 4. High-Level Features

1. **Authentication & Profile Management**:
   - Secure login and registration with validation of campus emails and minimum password length.
   - Non-sensitive profile inspection concealing passwords.
2. **Lost & Found Management (Full CRUD)**:
   - Report creation with automated unique ID generation.
   - Owner-level report editing (title, description, location) and cancellation/deletion.
3. **Multi-Criteria Search Engine**:
   - Free-text keyword search across item titles, descriptions, and campus landmarks.
   - Indexed Category search using Java `HashMap` for efficient retrieval.
   - Multi-parameter filtering (Category + Location + Date + Status + Keyword).
4. **Explainable 100-Point Match Algorithm**:
   - Category match: 25 points.
   - Campus location match / area overlap: 25 points.
   - Date proximity: 20 points.
   - Description keyword overlap (stopwords excluded): 30 points.
   - Detailed point breakdown with human-readable rationale.
5. **Formal Claim Workflow**:
   - Mandatory proof of ownership input (serial numbers, distinctive marks, lock screen PINs).
   - Guard rails preventing duplicate claims or claiming self-reported articles.
   - Two-phase resolution (PENDING $\rightarrow$ APPROVED / REJECTED) with automatic closure of linked reports.
6. **Concurrent Match Notification**:
   - Background Java `Thread` execution (`Runnable`) scanning active records upon report creation.
   - Synchronized thread-safe notification queue (`NotificationCenter`) presenting alerts upon dashboard load.
7. **File I/O Reporting**:
   - Formatted plain-text export (`campus_reports_export.txt`) for campus record-keeping and offline verification.
