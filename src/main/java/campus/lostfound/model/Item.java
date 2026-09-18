package campus.lostfound.model;

import campus.lostfound.enums.ItemCategory;
import campus.lostfound.enums.ItemStatus;

public abstract class Item {
    private int itemId;
    private String title;
    private String description;
    private ItemCategory category;
    private String location;
    private String itemDate; // YYYY-MM-DD
    private ItemStatus status;
    private int userId;
    private String reporterName;
    private String createdAt;

    public Item() {
        this.status = ItemStatus.OPEN;
    }

    public Item(int itemId, String title, String description, ItemCategory category,
                String location, String itemDate, ItemStatus status, int userId) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.itemDate = itemDate;
        this.status = (status != null) ? status : ItemStatus.OPEN;
        this.userId = userId;
    }

    // Abstract methods demonstrating polymorphism
    public abstract String getReportType();
    public abstract String getSpecialDetail();

    public void displaySummary() {
        System.out.printf("[%s #%d] %s (%s) | Status: %s | Location: %s | Date: %s%n",
                getReportType(), itemId, title, category, status, location, itemDate);
    }

    public void updateDetails(String title, String description, String location) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (description != null && !description.trim().isEmpty()) {
            this.description = description.trim();
        }
        if (location != null && !location.trim().isEmpty()) {
            this.location = location.trim();
        }
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
