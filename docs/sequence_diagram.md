# Sequence Diagram: Lost Item Reporting & Background Match Flow

This sequence diagram depicts the interaction between the user, CLI, service layer, background thread, match engine, and notification queue.

```mermaid
sequenceDiagram
    autonumber
    actor User as Student / User
    participant CLI as Main.java (CLI)
    participant ItemSvc as ItemService
    participant ItemRepo as ItemRepository
    participant Task as MatchFinderTask (Thread)
    participant MatchSvc as MatchService
    participant Notif as NotificationCenter
    participant DB as Database (MySQL/Cache)

    User->>CLI: Enter Lost Item Details (Title, Category, Location, Date, Desc)
    CLI->>ItemSvc: reportLostItem(...)
    ItemSvc->>ItemRepo: save(LostReport)
    ItemRepo->>DB: INSERT INTO items VALUES (...)
    DB-->>ItemRepo: return generated ID
    ItemRepo-->>ItemSvc: return saved LostReport (#5)
    ItemSvc-->>CLI: return LostReport (#5)
    CLI-->>User: Display "[+] Report created successfully! ID: #5"

    Note over CLI,Task: Multithreading: Spawn background matching thread
    CLI->>Task: new Thread(new MatchFinderTask(item, itemService, matchService)).start()
    Task->>ItemSvc: getFoundItems()
    ItemSvc->>ItemRepo: findByReportType("FOUND")
    ItemRepo-->>Task: return list of Found Items
    
    loop For each Found Item
        Task->>MatchSvc: evaluateMatch(lostItem, foundItem)
        MatchSvc-->>Task: return MatchResult (score, breakdown, reason)
    end

    opt Score >= 50
        Task->>Notif: addNotification("Match engine identified ... for #5")
        Note over Notif: Synchronized safe add
        Task-->>CLI: Print non-blocking [BACKGROUND ALERT]
    end

    User->>CLI: Returns to User Dashboard
    CLI->>Notif: retrievePendingNotifications()
    Notif-->>CLI: return and clear alerts
    CLI-->>User: Display pending alerts on Dashboard banner
```
