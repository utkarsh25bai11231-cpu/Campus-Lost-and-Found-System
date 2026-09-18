package campus.lostfound.model;

import campus.lostfound.enums.ItemCategory;
import campus.lostfound.enums.ItemStatus;

public class FoundReport extends Item {
    private String storageLocation;

    public FoundReport() {
        super();
    }

    public FoundReport(int itemId, String title, String description, ItemCategory category,
                       String location, String itemDate, ItemStatus status, int userId,
                       String storageLocation) {
        super(itemId, title, description, category, location, itemDate, status, userId);
        this.storageLocation = storageLocation;
    }

    @Override
    public String getReportType() {
        return "FOUND";
    }

    @Override
    public String getSpecialDetail() {
        return "Deposited / Kept at: " + (storageLocation != null ? storageLocation : "With Finder");
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }
}
