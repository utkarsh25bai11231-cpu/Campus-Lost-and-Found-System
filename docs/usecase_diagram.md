# Use Case Diagram

This diagram depicts system interactions for the three primary actors: **Student**, **Faculty**, and **Campus Staff**.

```mermaid
flowchart LR
    subgraph Actors
        Student(("Student"))
        Faculty(("Faculty"))
        Staff(("Staff / Admin"))
    end

    subgraph Lost & Found System
        UC1["Register Account / Login"]
        UC2["Report Lost Belonging"]
        UC3["Report Found Item"]
        UC4["Search & Filter Campus Items"]
        UC5["View Possible Matches & Scores"]
        UC6["Submit Ownership Claim with Proof"]
        UC7["Update / Cancel Own Reports"]
        UC8["Review & Approve/Reject Claims"]
        UC9["Export Records to File"]
    end

    Student --> UC1
    Student --> UC2
    Student --> UC3
    Student --> UC4
    Student --> UC5
    Student --> UC6
    Student --> UC7
    Student --> UC9

    Faculty --> UC1
    Faculty --> UC2
    Faculty --> UC3
    Faculty --> UC4
    Faculty --> UC5
    Faculty --> UC6
    Faculty --> UC7
    Faculty --> UC9

    Staff --> UC1
    Staff --> UC2
    Staff --> UC3
    Staff --> UC4
    Staff --> UC5
    Staff --> UC6
    Staff --> UC7
    Staff --> UC8
    Staff --> UC9
```

---

## Use Case Descriptions

- **UC1 (Register / Login)**: Authenticate or create role-specific profiles (Student regNo, Faculty empId, Staff staffId).
- **UC2 (Report Lost Belonging)**: Input title, description, category, location, date, reward amount, and unique markings.
- **UC3 (Report Found Item)**: Input title, description, category, location, date, and secure storage location.
- **UC4 (Search Items)**: Search via keywords or multi-criteria filters (Category, Location, Date, Status).
- **UC5 (View Possible Matches)**: Inspect 100-point algorithm breakdown and reasons.
- **UC6 (Submit Ownership Claim)**: Provide verifiable proof (PIN, invoice, marks) to request an item.
- **UC7 (Update / Cancel Reports)**: Edit details or close active reports created by the logged-in user.
- **UC8 (Review & Approve/Reject Claims)**: Handled by the finder or administrative staff to mark items as `CLAIMED`.
- **UC9 (Export Records)**: Generate an offline text file log of all items and claim records.
