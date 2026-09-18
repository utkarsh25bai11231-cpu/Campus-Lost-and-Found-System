# Project Statement
## Problem Statement
In a college campus students, faculty members and staff frequently lose or find personal belongings such as identity cards, books, calculators, wallets, keys, bags, electronic devices and other useful items. At present time, information about lost and found items may be shared through informal communication channels, student groups, security desks or personal communication. This can make it difficult to maintain a centralized record of reported items. Searching for a particular item can also become time consuming when the number of reports increases.

Another difficulty is identifying whether a reported lost item corresponds to an item that has already been found. The person who lost an item may not know that someone else has reported finding it.

Therefore, a simple centralized system is required to allow campus users to report lost and found items, search existing reports, identify possible matches and manage the process of claiming an item.

Hence, there is a need for an intelligent system that can assist to manage the process of Lost and Found, from registering the lost item to finding its matches and finally getting the exact item which was lost.

# Project Statement
The **Campus Lost and Found System** is a command line based Java application designed to manage lost and found item reports within a campus environment. The application allows users to register and log in, report lost or found items, then search and filter reports, identify possible matches between lost and found items and submit claims for found items.
The system is developed using Java and demonstrates important concepts from the Programming in Java course, including:
Object-Oriented Programming
* Classes and Objects
* Encapsulation
* Inheritance
* Method Overriding
* Runtime Polymorphism
* Interfaces and Abstract Classes
* Exception Handling
* Collections
* Multithreading
* Synchronization
* File Input/Output
* JDBC and Database Connectivity
* Enumerations
* Input Validation

A rule based matching mechanism is used to compare lost and found reports using category, location, date proximity and description similarity.

## Purpose of the Project
The primary purpose of this project is to provide a structured system for managing lost and found items on a college campus.
The proposed system aims to:

* Maintain organized records of lost and found items.
* Reduce the difficulty of searching for reported items.
* Help users identify potentially matching lost and found reports.
* Provide a structured claim process.
* Demonstrate practical implementation of Java programming concepts.
* Provide a command line application which can be executed without a graphical interface.

## Scope of the Project
The primary scope of this project is to develop a simple and organised campus level system. It would help students, faculty and staff to report, search, match and claim lost and found items using Java.

The current project is mainly focused on demonstrating these features through a Java command-line application. It is developed as an academic project to apply Java programming concepts such as object-oriented programming, collections, exception handling, file handling, JDBC, and multithreading to a practical campus problem.

### Included in this scope:
* User registration and login
* Different user types such as Student, Faculty and Staff
* Lost item reporting
* Found item reporting
* Viewing and managing personal reports
* Searching for items
* Filtering items by available criteria
* Rule based possible match detection
* Match score calculation
* Claim submission
* Claim review and status management
* Background matching using multithreading
* Synchronized notification handling
* File-based report/export functionality
* JDBC based database connectivity
* Input validation
* Exception handling
* Maven based project management, and
* Command line execution
### Not included in this scope:
Graphical User Interface (GUI)
Web application
Mobile application
REST API
Machine Learning or Deep Learning based matching
Computer Vision based image matching
Cloud deployment
External SMS or Email notification services, and
Online payment systems

## Target Users
This analyser is designed for a variety of users who need a simple and efficient system for collecting and managing information about the lost items and finding their matches in a certain time.

The intended users of the system are:

**Students:** Students can report lost or found belongings, search reports, check possible matches and submit claims.

**Faculty Members:** Faculty members can report and search their lost and found items and participate in the claim process.
**Staff Members:** Campus staff members can manage reports and participate in the lost and found workflow.
**Campus Administration / Security Staff:** This system provides a structured record that can support the management of lost and found items.
**Academic Evaluators:** This project also demonstrates practical application of Java programming concepts for academic evaluation under CSE2006.

## Approaching Method
The project follows a modular approach in which different responsibilities are separated into models, services, repositories, utilities, database components and exception classes.

* **Step 1 –** Requirement Analysis The requirements of a campus lost and found system were identified, including reporting, searching, matching, and claiming items.
* **Step 2 –** System Design The application was divided into multiple modules so that each module handles a specific responsibility.
* **Step 3 –** Object oriented Design Classes such as ‘User’, ‘Student’, ‘Faculty’, ‘Staff’, ‘Item’, ‘LostReport’, ‘FoundReport’ and ‘Claim’ are used to represent system entities. Inheritance and polymorphism are used to represent different types of users.
* **Step 4 –** Data Management Collections such as ‘ArrayList’ and ‘HashMap’ are used to manage application data and indexes. JDBC is used to provide database connectivity.
* **Step 5 –** Search and Matching Users can search and filter reported items. A rule-based matching algorithm calculates a score between lost and found reports.

In Statement.md it is implemented as:
| Matching Factor | Maximum Score |
|---|---:|
| Category | 25 | | Location | 25 |
| Date Proximity | 20 |
| Description Similarity | 30 |
|Total | 100 |
* **Step 6 –** Validation and Exception Handling Input validation is performed for fields such as names, email addresses, dates and numerical values. Custom exceptions are used to handle application specific errors.
* **Step 7 –** Multithreading Background matching tasks are executed using Java threads so that possible matches can be processed separately from the main command-line workflow. Synchronization is used where shared notification data requires controlled access.
* **Step 8 –** File Handling Java file I/O classes are used for exporting or storing application information.
* **Step 9 –** Testing The application is tested through command line execution and functional workflows such as registration, login, reporting, searching, matching and claiming.

## High Level Features
* User Management
* Registration
* Login
* User type selection
* Profile related operations
* Lost Item Management
* Users can report items that they have lost by providing relevant information
such as:
- Item name
- Category
- Location
- Date
- Description
- Found Item Management
* Users can register items they have found using similar item information.
* Search and Filtering
* Users can search reported items using keywords and filters such as category and other available criteria.
* Possible Match Detection

The system compares lost and found reports and generates a match score. The score is calculated using:
```text
Category Match       → 25 marks
Location Match       → 25 marks
Date Similarity      → 20 marks
Description Match    → 30 marks
--------------------------------
Maximum Score        → 100 marks

Possible matches can be classified based on the calculated score.

* Claim Management

Users can submit claims for found items.
Claims can move through different statuses such as:

- Pending
- Approved
- Rejected

* Multithreading

Matching tasks can run in the background using Java's multithreading functionality.

* File Export
The system provides file-based export functionality for application information and reports.

```text
Start
↓
Register / Login
↓
Main Menu
|
+---- Report Lost Item
|
+---- Report Found Item
|
+---- Search Items
|
+---- Find Possible Matches
|
+---- Manage Reports
|
+---- Claim Found Item
|
+---- Export Information
↓
Logout / Exit
```
## Expected Outcomes
This project is expected to outcome the followings:
A functional command line lost and found management system.
Organized handling of lost and found reports.
Faster searching of available reports.
Automated rule based identification of possible matches.
Structured claim management.
Demonstration of major Java programming concepts.
Modular and maintainable source code.
Proper validation and exception handling.
Practical use of Java collections, file handling, JDBC and multithreading.

## Technological Overview
Programming Language: Java
Build Tool: Apache Maven
Database: MySQL with JDBC
Development Environment: Visual Studio Code / Command Line
Java Concepts Used:
OOP
Inheritance
Encapsulation
Polymorphism
Abstract Classes
Interfaces
Collections
Exception Handling
Multithreading
Synchronization
File I/O
JDBC
Enums
Packages
## System Architecture
This system follows a layered modular architecture:
+-----------------------------+
|       Command Line UI       |
|      Menu and Input         |
+--------------+--------------+
|
v
+-----------------------------+
|          Services           |
| User | Item | Search        |
| Match | Claim               |
+--------------+--------------+
|
v
+-----------------------------+
|        Repositories         |
| User | Item | Claim         |
+--------------+--------------+
/            \
v              v
+---------------+   +-------------+
| JDBC Database |   |  File I/O   |
|    MySQL      |   |   Export    |
+---------------+   +-------------+

## Conclusions and Impact
Significance:
The project provides a structured approach to managing campus lost and found information.
Instead of depending only on informal communication, the application provides a centralized workflow for reporting, searching, matching, and claiming items.
From an academic perspective, the project demonstrates how Java programming concepts can be combined to develop a practical application rather than implementing concepts independently.

Limitations:
The current implementation has some limitations:
It is command-line based.
It does not provide a web or mobile interface.
Matching is rule-based rather than machine-learning based.
External notification services are not included.
The current database implementation is intended for the project environment rather than large-scale deployment.
## Future Enhancements
Possible future enhancements include:
Development of a web-based interface.
Development of a mobile application.
Integration with institutional authentication.
Email and SMS notifications.
Image-based item matching.
Machine-learning-based matching.
Cloud database deployment.
Administrative dashboard.
Advanced analytics and reporting.
Role-based administrative access.
## Conclusion:
The Campus Lost and Found System provides a structured command-line solution for managing lost and found items within a campus environment.
The project combines multiple Java concepts including object-oriented programming, collections, exception handling, multithreading, synchronization, file handling, and JDBC.
The application demonstrates how these concepts can be integrated into a single practical system with separate modules for users, reports, searching, matching, and claims.

Thank You!
