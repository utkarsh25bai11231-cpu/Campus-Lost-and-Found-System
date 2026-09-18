# System Architecture Diagram

This diagram represents the actual 4-tier architectural structure of the **Campus Lost and Found System**.

```mermaid
graph TD
    subgraph Client Layer [1. Presentation / Terminal CLI Layer]
        MainCLI["Main.java (Scanner Interactive CLI)"]
        GuestMenu["Guest Menu (Public Browse, Auth)"]
        UserDash["User Dashboard (Role-based actions)"]
        MainCLI --> GuestMenu
        MainCLI --> UserDash
    end

    subgraph Service Layer [2. Core Business Logic Layer]
        UserService["UserService.java\n- Authentication\n- Registration\n- Profile inspection"]
        ItemService["ItemService.java\n- Report Lost/Found\n- Update & Cancel CRUD\n- Multi-Criteria Search"]
        MatchService["MatchService.java\n- 100-pt Rule Engine\n- Reason generation"]
        ClaimService["ClaimService.java\n- Claim validation\n- Approval & Status"]
    end

    subgraph Concurrency [3. Multithreading & Notifications]
        MatchFinderTask["MatchFinderTask.java\n(implements Runnable)"]
        NotificationCenter["NotificationCenter.java\n(Thread-Safe Synchronized Queue)"]
        ItemService -.->|Spawns Thread| MatchFinderTask
        MatchFinderTask -->|Evaluates| MatchService
        MatchFinderTask -->|Safe Push| NotificationCenter
        NotificationCenter -->|Pulls Alerts| UserDash
    end

    subgraph Data Access Layer [4. Repository & In-Memory Cache]
        UserRepo["UserRepository.java\n- HashMap emailIndex\n- HashMap idIndex"]
        ItemRepo["ItemRepository.java\n- HashMap categoryIndex\n- HashMap idIndex"]
        ClaimRepo["ClaimRepository.java\n- Active claim checks"]
    end

    subgraph Persistence Layer [5. Persistence & Output]
        DBConn["DatabaseConnection.java\n(DriverManager / PreparedStatement)"]
        MySQL[("MySQL Database\n(campus_lostfound)")]
        FileExp["FileExporter.java\n(FileWriter / PrintWriter)"]
        TextFile[("campus_reports_export.txt")]
    end

    UserDash --> UserService
    UserDash --> ItemService
    UserDash --> MatchService
    UserDash --> ClaimService
    UserDash --> FileExp

    UserService --> UserRepo
    ItemService --> ItemRepo
    MatchService --> ItemRepo
    ClaimService --> ClaimRepo
    ClaimService --> ItemRepo

    UserRepo --> DBConn
    ItemRepo --> DBConn
    ClaimRepo --> DBConn
    DBConn -->|JDBC| MySQL
    FileExp --> TextFile
```

---

## Architecture Components

1. **Presentation Layer (`campus.lostfound.Main`)**: Handles terminal input parsing, menu navigation, and input validation without external UI libraries.
2. **Business Service Layer (`campus.lostfound.service.*`)**: Encapsulates domain logic, validation rules, 100-point matching calculations, and claim transitions.
3. **Concurrency Layer (`campus.lostfound.util.*`)**: Asynchronously evaluates match candidates upon item creation and stores alerts in a thread-safe synchronized queue.
4. **Data Access Layer (`campus.lostfound.repository.*`)**: Coordinates JDBC queries with in-memory HashMap indices for fast lookups.
5. **Persistence Layer**: Relational MySQL database backed by `DatabaseConnection` with zero-crash in-memory fallback.
