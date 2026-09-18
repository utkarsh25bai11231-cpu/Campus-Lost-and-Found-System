package campus.lostfound;

import campus.lostfound.enums.ClaimStatus;
import campus.lostfound.enums.ItemCategory;
import campus.lostfound.enums.ItemStatus;
import campus.lostfound.enums.UserType;
import campus.lostfound.model.*;
import campus.lostfound.service.ClaimService;
import campus.lostfound.service.ItemService;
import campus.lostfound.service.MatchService;
import campus.lostfound.service.UserService;
import campus.lostfound.util.DateUtil;
import campus.lostfound.util.FileExporter;
import campus.lostfound.util.MatchFinderTask;
import campus.lostfound.util.NotificationCenter;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ItemService itemService = new ItemService();
    private static final MatchService matchService = new MatchService();
    private static final ClaimService claimService = new ClaimService();

    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("        CAMPUS LOST AND FOUND SYSTEM - VIT BHOPAL         ");
        System.out.println("           Course: CSE2006 Programming in Java            ");
        System.out.println("==========================================================");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = runGuestMenu();
            } else {
                runUserDashboard();
            }
        }

        System.out.println("\nThank you for using Campus Lost & Found System. Goodbye!");
        scanner.close();
    }

    // ==========================================================
    // 1. GUEST MENU (When not logged in)
    // ==========================================================
    private static boolean runGuestMenu() {
        System.out.println("\n==========================================================");
        System.out.println("                     MAIN MENU                            ");
        System.out.println("==========================================================");
        System.out.println("1. Login");
        System.out.println("2. Register New Account");
        System.out.println("3. Search & Browse Campus Items (Public)");
        System.out.println("4. Find Possible Matches (Public Campus Overview)");
        System.out.println("5. Exit");
        System.out.println("==========================================================");

        int choice = readInt("Enter your choice (1-5): ", 1, 5);
        try {
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    handleSearchItems();
                    break;
                case 4:
                    handleCampusMatches();
                    break;
                case 5:
                    return false;
            }
        } catch (Exception e) {
            System.out.println("(!) Error: " + e.getMessage());
        }
        return true;
    }

    // ==========================================================
    // 2. USER DASHBOARD (When logged in)
    // ==========================================================
    private static void runUserDashboard() {
        // Check for any background match alerts stored by MatchFinderTask
        if (NotificationCenter.hasNotifications()) {
            List<String> alerts = NotificationCenter.retrievePendingNotifications();
            System.out.println("\n----------------------------------------------------------");
            System.out.println("🔔 PENDING BACKGROUND NOTIFICATIONS:");
            for (String alert : alerts) {
                System.out.println("   * " + alert);
            }
            System.out.println("----------------------------------------------------------");
        }

        System.out.println("\n==========================================================");
        System.out.println("                      USER DASHBOARD                      ");
        System.out.printf("Logged in as: %s | Role: %s (%s)%n",
                currentUser.getName(), currentUser.getUserType(), currentUser.getIdentifier());
        System.out.println("==========================================================");
        System.out.println("1. Report Lost Item");
        System.out.println("2. Report Found Item");
        System.out.println("3. Search Items (Keyword / Multi-Criteria Filter)");
        System.out.println("4. Find Possible Matches (Smart Rule-Based Engine)");
        System.out.println("5. Manage My Reports (View / Update / Cancel / Delete)");
        System.out.println("6. Claim Management (Submit Claim / Review Claims)");
        System.out.println("7. Export Reports to File");
        System.out.println("8. View My Profile");
        System.out.println("9. Logout");
        System.out.println("==========================================================");

        int choice = readInt("Enter your choice (1-9): ", 1, 9);
        try {
            switch (choice) {
                case 1:
                    handleReportLostItem();
                    break;
                case 2:
                    handleReportFoundItem();
                    break;
                case 3:
                    handleSearchItems();
                    break;
                case 4:
                    handlePossibleMatches();
                    break;
                case 5:
                    handleManageMyReports();
                    break;
                case 6:
                    handleClaimManagement();
                    break;
                case 7:
                    handleExportReports();
                    break;
                case 8:
                    handleViewProfile();
                    break;
                case 9:
                    handleLogout();
                    break;
            }
        } catch (Exception e) {
            System.out.println("(!) Error: " + e.getMessage());
        }
    }

    // ==========================================================
    // USER REGISTRATION & AUTHENTICATION
    // ==========================================================
    private static void handleRegister() {
        System.out.println("\n--- NEW USER REGISTRATION ---");
        String name = readNonEmptyString("Enter full name: ");
        String email = readEmail("Enter campus email: ");
        String password = readNonEmptyString("Enter password (min 4 characters): ");
        while (password.length() < 4) {
            System.out.println("(!) Password too short. Minimum 4 characters required.");
            password = readNonEmptyString("Enter password (min 4 characters): ");
        }
        String phone = readNonEmptyString("Enter phone number: ");
        String dept = readNonEmptyString("Enter department (e.g. SCOPE, SCSE, SENSE, Civil): ");

        System.out.println("\nSelect Role:");
        System.out.println("1. Student");
        System.out.println("2. Faculty");
        System.out.println("3. Staff");
        int typeChoice = readInt("Select (1-3): ", 1, 3);

        User newUser;
        if (typeChoice == 1) {
            String regNo = readNonEmptyString("Enter Registration Number (e.g. 23BCE10001): ");
            String hostel = readNonEmptyString("Enter Hostel Block & Room (or type 'Day Scholar'): ");
            newUser = new Student(0, name, email, password, phone, dept, regNo, hostel);
        } else if (typeChoice == 2) {
            String empId = readNonEmptyString("Enter Employee ID (e.g. FAC1024): ");
            String cabin = readNonEmptyString("Enter Cabin Location (e.g. AB-1 Room 302): ");
            newUser = new Faculty(0, name, email, password, phone, dept, empId, cabin);
        } else {
            String staffId = readNonEmptyString("Enter Staff ID (e.g. STF205): ");
            String designation = readNonEmptyString("Enter Designation (e.g. Lab Assistant, Security): ");
            newUser = new Staff(0, name, email, password, phone, dept, staffId, designation);
        }

        try {
            User registered = userService.registerUser(newUser);
            currentUser = registered;
            System.out.println("\n[+] Registration successful! You are now logged in.");
            currentUser.displayProfile();
        } catch (Exception e) {
            System.out.println("(!) Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        String email = readNonEmptyString("Enter campus email: ");
        String password = readNonEmptyString("Enter password: ");

        try {
            currentUser = userService.login(email, password);
            System.out.println("\n[+] Login successful! Welcome, " + currentUser.getName() + ".");
        } catch (Exception e) {
            System.out.println("(!) Login failed: " + e.getMessage());
        }
    }

    private static void handleViewProfile() {
        System.out.println("\n--- LOGGED-IN USER PROFILE ---");
        if (currentUser != null) {
            currentUser.displayProfile();
        }
    }

    private static void handleLogout() {
        if (currentUser != null) {
            System.out.println("\n[+] Logged out " + currentUser.getName() + " successfully.");
            currentUser = null;
        }
    }

    // ==========================================================
    // REPORTING LOST & FOUND ITEMS
    // ==========================================================
    private static void handleReportLostItem() throws Exception {
        System.out.println("\n--- REPORT A LOST ITEM ---");
        String title = readNonEmptyString("Enter Item Name / Title: ");
        ItemCategory category = selectCategory();
        String location = readNonEmptyString("Enter Location where lost (e.g. Library 2nd Floor, AB-1 Lab 102): ");
        String date = readDateInput("Enter Date lost (YYYY-MM-DD) [Press Enter for today (" + DateUtil.getToday() + ")]: ");
        String desc = readNonEmptyString("Enter Detailed Description (colors, unique markings, contents): ");
        double reward = readDouble("Enter Reward amount in INR (0 for none): ", 0, 50000);
        System.out.print("Does it have unique identifiable markings/serial numbers (yes/no)? ");
        boolean identifiable = scanner.nextLine().trim().equalsIgnoreCase("yes");

        LostReport report = itemService.reportLostItem(title, desc, category, location, date,
                currentUser.getUserId(), reward, identifiable);

        System.out.println("\n[+] Lost item report created successfully! Report ID: #" + report.getItemId());

        // Spawn background thread to find matches non-blockingly
        Thread bgThread = new Thread(new MatchFinderTask(report, itemService, matchService));
        bgThread.start();
    }

    private static void handleReportFoundItem() throws Exception {
        System.out.println("\n--- REPORT A FOUND ITEM ---");
        String title = readNonEmptyString("Enter Item Name / Title: ");
        ItemCategory category = selectCategory();
        String location = readNonEmptyString("Enter Location where found: ");
        String date = readDateInput("Enter Date found (YYYY-MM-DD) [Press Enter for today (" + DateUtil.getToday() + ")]: ");
        String desc = readNonEmptyString("Enter Detailed Description: ");
        String storage = readNonEmptyString("Where is the item deposited / kept (e.g. Security Main Gate, AB-1 Proctor Office)? ");

        FoundReport report = itemService.reportFoundItem(title, desc, category, location, date,
                currentUser.getUserId(), storage);

        System.out.println("\n[+] Found item report created successfully! Report ID: #" + report.getItemId());

        // Spawn background thread to find matches non-blockingly
        Thread bgThread = new Thread(new MatchFinderTask(report, itemService, matchService));
        bgThread.start();
    }

    // ==========================================================
    // SEARCH & FILTERING (Collections: ArrayList & HashMap)
    // ==========================================================
    private static void handleSearchItems() {
        System.out.println("\n--- SEARCH & FILTER ITEMS ---");
        System.out.println("1. View All Campus Reports");
        System.out.println("2. Simple Keyword Search (Title, Description, or Location)");
        System.out.println("3. Quick Category Filter (Using HashMap Category Index)");
        System.out.println("4. Multi-Criteria Search (Category + Location + Date + Status + Keyword)");
        int mode = readInt("Select option (1-4): ", 1, 4);

        List<Item> results;
        if (mode == 2) {
            String kw = readNonEmptyString("Enter keyword to search: ");
            results = itemService.search(kw);
        } else if (mode == 3) {
            ItemCategory cat = selectCategory();
            results = itemService.search(cat, "");
        } else if (mode == 4) {
            System.out.println("\n[Multi-Criteria Filter - Press Enter to leave any filter open/empty]");
            System.out.print("Keyword (Title/Description): ");
            String kw = scanner.nextLine().trim();

            System.out.print("Filter by specific category? (yes/no): ");
            ItemCategory cat = null;
            if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                cat = selectCategory();
            }

            System.out.print("Location contains: ");
            String loc = scanner.nextLine().trim();

            System.out.print("Date (YYYY-MM-DD): ");
            String dt = scanner.nextLine().trim();

            System.out.print("Filter by status (OPEN / CLAIM_PENDING / CLAIMED / CLOSED) [or Enter for all]: ");
            String stStr = scanner.nextLine().trim();
            ItemStatus status = null;
            if (!stStr.isEmpty()) {
                status = ItemStatus.fromString(stStr);
            }

            results = itemService.search(kw, cat, loc, dt, status);
        } else {
            results = itemService.getAllItems();
        }

        displayItemTable(results);
    }

    // ==========================================================
    // SMART RULE-BASED MATCH DETECTION
    // ==========================================================
    private static void handlePossibleMatches() {
        System.out.println("\n--- SMART RULE-BASED MATCH ENGINE ---");
        System.out.println("1. Find Matches for My Lost Items");
        System.out.println("2. Scan All Open Lost & Found Reports Across Campus");
        int sub = readInt("Select choice (1-2): ", 1, 2);

        if (sub == 1) {
            List<Item> myItems = itemService.getItemsByUser(currentUser.getUserId());
            List<Item> myLostItems = myItems.stream()
                    .filter(it -> "LOST".equalsIgnoreCase(it.getReportType()) && it.getStatus() != ItemStatus.CLAIMED)
                    .toList();

            if (myLostItems.isEmpty()) {
                System.out.println("(!) You do not have any active (unclaimed) lost item reports.");
                return;
            }

            System.out.println("\nYour Active Lost Items:");
            for (Item item : myLostItems) {
                System.out.printf("[#%d] %s (%s, %s)%n", item.getItemId(), item.getTitle(), item.getCategory(), item.getItemDate());
            }

            int lostId = readInt("Enter Lost Item ID to match: ", 1, Integer.MAX_VALUE);
            try {
                Item selected = itemService.getItemById(lostId);
                if (selected.getUserId() != currentUser.getUserId()) {
                    System.out.println("(!) That item does not belong to your account.");
                    return;
                }

                List<Item> foundItems = itemService.getFoundItems();
                List<MatchResult> matches = matchService.findMatchesForLostItem(selected, foundItems, 40);

                if (matches.isEmpty()) {
                    System.out.println("\nNo matches detected with confidence score >= 40/100. We will keep scanning in the background!");
                } else {
                    System.out.printf("%nFound %d possible match(es):%n", matches.size());
                    for (MatchResult res : matches) {
                        res.displayExplanation();
                    }
                }
            } catch (Exception e) {
                System.out.println("(!) Error: " + e.getMessage());
            }
        } else {
            handleCampusMatches();
        }
    }

    private static void handleCampusMatches() {
        List<Item> lostItems = itemService.getLostItems();
        List<Item> foundItems = itemService.getFoundItems();
        List<MatchResult> matches = matchService.findAllPossibleMatches(lostItems, foundItems, 45);

        if (matches.isEmpty()) {
            System.out.println("\nNo strong matches detected across open campus reports with score >= 45/100.");
        } else {
            System.out.printf("%nFound %d possible match pair(s) across campus:%n", matches.size());
            for (MatchResult res : matches) {
                res.displayExplanation();
            }
        }
    }

    // ==========================================================
    // MANAGE MY REPORTS (CRUD: View, Update, Cancel, Delete)
    // ==========================================================
    private static void handleManageMyReports() {
        System.out.println("\n--- MANAGE MY REPORTS ---");
        List<Item> myItems = itemService.getItemsByUser(currentUser.getUserId());
        if (myItems.isEmpty()) {
            System.out.println("You have not reported any items yet.");
            return;
        }

        displayItemTable(myItems);
        System.out.println("1. View Full Details of a Report");
        System.out.println("2. Edit / Update a Report (Title, Description, Location)");
        System.out.println("3. Cancel a Report (Mark as CLOSED)");
        System.out.println("4. Delete a Report (Remove Permanently)");
        System.out.println("5. Back to Dashboard");
        int act = readInt("Select action (1-5): ", 1, 5);

        if (act == 5) return;

        int itemId = readInt("Enter Report / Item ID: ", 1, Integer.MAX_VALUE);
        try {
            Item item = itemService.getItemById(itemId);
            if (act == 1) {
                System.out.println("\n--------------------------------------------------");
                System.out.printf("REPORT [#%d] - %s%n", item.getItemId(), item.getReportType());
                System.out.println("Title          : " + item.getTitle());
                System.out.println("Category       : " + item.getCategory().name());
                System.out.println("Status         : " + item.getStatus().name());
                System.out.println("Location       : " + item.getLocation());
                System.out.println("Date           : " + item.getItemDate());
                System.out.println("Description    : " + item.getDescription());
                System.out.println("Special Details: " + item.getSpecialDetail());
                System.out.println("--------------------------------------------------");
            } else if (act == 2) {
                System.out.println("Enter new details (press Enter to keep current):");
                System.out.print("New Title [" + item.getTitle() + "]: ");
                String t = scanner.nextLine().trim();
                if (t.isEmpty()) t = item.getTitle();

                System.out.print("New Description [" + item.getDescription() + "]: ");
                String d = scanner.nextLine().trim();
                if (d.isEmpty()) d = item.getDescription();

                System.out.print("New Location [" + item.getLocation() + "]: ");
                String l = scanner.nextLine().trim();
                if (l.isEmpty()) l = item.getLocation();

                itemService.updateItemDetails(itemId, currentUser.getUserId(), t, d, l);
                System.out.println("[+] Report updated successfully!");
            } else if (act == 3) {
                itemService.cancelReport(itemId, currentUser.getUserId());
                System.out.println("[+] Report marked as CLOSED.");
            } else if (act == 4) {
                System.out.print("Are you sure you want to permanently delete this report? (yes/no): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                    itemService.deleteReport(itemId, currentUser.getUserId());
                    System.out.println("[+] Report permanently deleted.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
        } catch (Exception e) {
            System.out.println("(!) Operation failed: " + e.getMessage());
        }
    }

    // ==========================================================
    // CLAIM MANAGEMENT
    // ==========================================================
    private static void handleClaimManagement() {
        System.out.println("\n--- CLAIM MANAGEMENT ---");
        System.out.println("1. Submit a Claim for a Found Item");
        System.out.println("2. View My Submitted Claims");
        System.out.println("3. Review Claims for Items I Found (Approve / Reject)");
        int choice = readInt("Select choice (1-3): ", 1, 3);

        if (choice == 1) {
            List<Item> openFound = itemService.getFoundItems().stream()
                    .filter(i -> i.getStatus() == ItemStatus.OPEN || i.getStatus() == ItemStatus.CLAIM_PENDING)
                    .toList();

            if (openFound.isEmpty()) {
                System.out.println("No open found items available for claiming.");
                return;
            }

            System.out.println("\nAvailable Found Items:");
            displayItemTable(openFound);

            int foundId = readInt("Enter Found Item ID you want to claim: ", 1, Integer.MAX_VALUE);
            System.out.print("Enter your Lost Item ID (or 0 if you did not report one): ");
            int lostId = 0;
            try {
                String lStr = scanner.nextLine().trim();
                if (!lStr.isEmpty()) lostId = Integer.parseInt(lStr);
            } catch (NumberFormatException ignored) {
            }

            String proof = readNonEmptyString("Enter Proof of Ownership (e.g. lock screen PIN, invoice, serial number, stickers): ");

            try {
                Claim claim = claimService.submitClaim(lostId, foundId, currentUser.getUserId(), proof);
                System.out.println("\n[+] Claim #" + claim.getClaimId() + " submitted successfully! Status: PENDING.");
                System.out.println("The finder or campus security will verify your proof details.");
            } catch (Exception e) {
                System.out.println("(!) Claim submission failed: " + e.getMessage());
            }
        } else if (choice == 2) {
            List<Claim> myClaims = claimService.getClaimsByClaimant(currentUser.getUserId());
            if (myClaims.isEmpty()) {
                System.out.println("You have not submitted any claims.");
            } else {
                System.out.println("\nYour Submitted Claims:");
                for (Claim c : myClaims) {
                    c.displaySummary();
                }
            }
        } else {
            // Review claims for items reported by current user or staff
            List<Claim> allClaims = claimService.getAllClaims();
            List<Claim> reviewable = allClaims.stream()
                    .filter(c -> {
                        try {
                            Item foundItem = itemService.getItemById(c.getFoundItemId());
                            return foundItem.getUserId() == currentUser.getUserId() || currentUser.getUserType() == UserType.STAFF;
                        } catch (Exception e) {
                            return false;
                        }
                    }).toList();

            if (reviewable.isEmpty()) {
                System.out.println("No pending claims for items reported by you or under staff administration.");
                return;
            }

            System.out.println("\nClaims Available for Review:");
            for (Claim c : reviewable) {
                c.displaySummary();
            }

            int claimId = readInt("Enter Claim ID to process (0 to cancel): ", 0, Integer.MAX_VALUE);
            if (claimId == 0) return;

            System.out.println("1. Approve Claim (Ownership verified, item handed over)");
            System.out.println("2. Reject Claim (Insufficient / mismatched proof)");
            int act = readInt("Choice (1-2): ", 1, 2);

            try {
                if (act == 1) {
                    claimService.approveClaim(claimId, currentUser.getUserId());
                    System.out.println("[+] Claim #" + claimId + " approved! Item status updated to CLAIMED.");
                } else {
                    claimService.rejectClaim(claimId);
                    System.out.println("[-] Claim #" + claimId + " rejected.");
                }
            } catch (Exception e) {
                System.out.println("(!) Error processing claim: " + e.getMessage());
            }
        }
    }

    // ==========================================================
    // FILE I/O: EXPORT REPORTS
    // ==========================================================
    private static void handleExportReports() {
        System.out.println("\n--- EXPORT SYSTEM REPORTS ---");
        String filename = "campus_reports_export.txt";
        List<Item> items = itemService.getAllItems();
        List<Claim> claims = claimService.getAllClaims();

        boolean success = FileExporter.exportReportsToFile(items, claims, filename);
        if (success) {
            System.out.println("[+] Successfully exported " + items.size() + " items and " + claims.size() + " claims to: " + filename);
        } else {
            System.out.println("(!) Failed to export reports to file.");
        }
    }

    // ==========================================================
    // INPUT VALIDATION HELPERS (Handles invalid input gracefully)
    // ==========================================================
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("(!) Input cannot be empty. Please enter a valid value.");
        }
    }

    private static String readEmail(String prompt) {
        while (true) {
            String email = readNonEmptyString(prompt);
            if (email.contains("@") && email.contains(".")) {
                return email;
            }
            System.out.println("(!) Please enter a valid email address (e.g., student@vitbhopal.ac.in).");
        }
    }

    private static String readDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return DateUtil.getToday();
            }
            if (DateUtil.isValidDate(input)) {
                return input;
            }
            System.out.println("(!) Invalid date format. Please use YYYY-MM-DD (e.g., 2026-09-17).");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("(!) Number out of range. Please enter between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("(!) Invalid number. Please enter digits only.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("(!) Amount out of range. Please enter between %.2f and %.2f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("(!) Invalid decimal number.");
            }
        }
    }

    private static ItemCategory selectCategory() {
        System.out.println("Select Item Category:");
        ItemCategory[] categories = ItemCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), categories[i].name());
        }
        int idx = readInt("Enter category number (1-" + categories.length + "): ", 1, categories.length);
        return categories[idx - 1];
    }

    private static void displayItemTable(List<Item> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("No items found matching criteria.");
            return;
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-6s | %-24s | %-16s | %-12s | %-18s | %-10s%n",
                "ID", "TYPE", "TITLE", "CATEGORY", "STATUS", "LOCATION", "DATE");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Item item : items) {
            String title = item.getTitle();
            if (title.length() > 24) title = title.substring(0, 21) + "...";
            String loc = item.getLocation();
            if (loc.length() > 18) loc = loc.substring(0, 15) + "...";

            System.out.printf("%-5d | %-6s | %-24s | %-16s | %-12s | %-18s | %-10s%n",
                    item.getItemId(), item.getReportType(), title, item.getCategory().name(),
                    item.getStatus().name(), loc, item.getItemDate());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }
}
