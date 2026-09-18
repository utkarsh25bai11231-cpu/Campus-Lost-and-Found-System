package campus.lostfound.service;

import campus.lostfound.enums.ItemCategory;
import campus.lostfound.enums.ItemStatus;
import campus.lostfound.exception.InvalidItemException;
import campus.lostfound.model.FoundReport;
import campus.lostfound.model.Item;
import campus.lostfound.model.LostReport;
import campus.lostfound.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService() {
        this.itemRepository = new ItemRepository();
    }

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public LostReport reportLostItem(String title, String description, ItemCategory category,
                                     String location, String itemDate, int userId,
                                     double rewardAmount, boolean isIdentifiable) throws InvalidItemException {
        validateCommonItemFields(title, description, location, itemDate);

        LostReport report = new LostReport(0, title.trim(), description.trim(), category,
                location.trim(), itemDate.trim(), ItemStatus.OPEN, userId, rewardAmount, isIdentifiable);

        return (LostReport) itemRepository.save(report);
    }

    public FoundReport reportFoundItem(String title, String description, ItemCategory category,
                                       String location, String itemDate, int userId,
                                       String storageLocation) throws InvalidItemException {
        validateCommonItemFields(title, description, location, itemDate);

        FoundReport report = new FoundReport(0, title.trim(), description.trim(), category,
                location.trim(), itemDate.trim(), ItemStatus.OPEN, userId,
                (storageLocation != null ? storageLocation.trim() : "With Finder"));

        return (FoundReport) itemRepository.save(report);
    }

    public Item getItemById(int itemId) throws InvalidItemException {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new InvalidItemException("Item not found with ID: " + itemId);
        }
        return item;
    }

    public void updateItemDetails(int itemId, int requesterUserId, String title, String description, String location)
            throws InvalidItemException {
        Item item = getItemById(itemId);

        if (item.getUserId() != requesterUserId) {
            throw new InvalidItemException("Permission denied: You can only edit reports created by yourself.");
        }
        if (item.getStatus() == ItemStatus.CLAIMED || item.getStatus() == ItemStatus.CLOSED) {
            throw new InvalidItemException("Cannot edit report: Item has already been " + item.getStatus().name() + ".");
        }

        validateCommonItemFields(title, description, location, item.getItemDate());
        boolean success = itemRepository.updateItem(itemId, title.trim(), description.trim(), location.trim());
        if (!success) {
            throw new InvalidItemException("Failed to update item with ID: " + itemId);
        }
    }

    public void cancelReport(int itemId, int requesterUserId) throws InvalidItemException {
        Item item = getItemById(itemId);

        if (item.getUserId() != requesterUserId) {
            throw new InvalidItemException("Permission denied: You can only cancel reports created by yourself.");
        }
        if (item.getStatus() == ItemStatus.CLAIMED) {
            throw new InvalidItemException("Cannot cancel report: Item has already been claimed and resolved.");
        }

        // Mark as CLOSED or delete
        itemRepository.updateStatus(itemId, ItemStatus.CLOSED);
    }

    public void deleteReport(int itemId, int requesterUserId) throws InvalidItemException {
        Item item = getItemById(itemId);

        if (item.getUserId() != requesterUserId) {
            throw new InvalidItemException("Permission denied: You can only delete reports created by yourself.");
        }
        if (item.getStatus() == ItemStatus.CLAIMED) {
            throw new InvalidItemException("Cannot delete report: Item has already been claimed and resolved.");
        }

        boolean deleted = itemRepository.deleteItem(itemId);
        if (!deleted) {
            throw new InvalidItemException("Failed to delete item with ID: " + itemId);
        }
    }

    public void updateItemStatus(int itemId, ItemStatus newStatus) throws InvalidItemException {
        getItemById(itemId); // Validates existence
        itemRepository.updateStatus(itemId, newStatus);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getLostItems() {
        return itemRepository.findByReportType("LOST");
    }

    public List<Item> getFoundItems() {
        return itemRepository.findByReportType("FOUND");
    }

    public List<Item> getItemsByUser(int userId) {
        return itemRepository.findByUserId(userId);
    }

    // Overloaded search 1: Keyword across title, description, location
    public List<Item> search(String keyword) {
        List<Item> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return itemRepository.findAll();
        }

        String query = keyword.trim().toLowerCase();
        for (Item item : itemRepository.findAll()) {
            boolean matchesTitle = item.getTitle() != null && item.getTitle().toLowerCase().contains(query);
            boolean matchesDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(query);
            boolean matchesLoc = item.getLocation() != null && item.getLocation().toLowerCase().contains(query);

            if (matchesTitle || matchesDesc || matchesLoc) {
                results.add(item);
            }
        }
        return results;
    }

    // Overloaded search 2: Category and location keyword
    public List<Item> search(ItemCategory category, String locationKeyword) {
        // Leverages HashMap category index for fast lookup
        List<Item> candidates = (category != null) ? itemRepository.findByCategoryFromIndex(category) : itemRepository.findAll();

        List<Item> results = new ArrayList<>();
        String locQuery = (locationKeyword != null) ? locationKeyword.trim().toLowerCase() : "";

        for (Item item : candidates) {
            boolean locMatches = locQuery.isEmpty() || (item.getLocation() != null && item.getLocation().toLowerCase().contains(locQuery));
            if (locMatches) {
                results.add(item);
            }
        }
        return results;
    }

    // Multi-criteria search: Category, Location, Date, Status, and Keyword
    public List<Item> search(String keyword, ItemCategory category, String location, String date, ItemStatus status) {
        // First retrieve candidates via HashMap index if category filter is present
        List<Item> candidates;
        if (category != null) {
            candidates = itemRepository.findByCategoryFromIndex(category);
        } else {
            candidates = itemRepository.findAll();
        }

        List<Item> filtered = new ArrayList<>();
        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        String loc = (location != null) ? location.trim().toLowerCase() : "";
        String dt = (date != null) ? date.trim() : "";

        for (Item item : candidates) {
            boolean matchesKw = kw.isEmpty()
                    || (item.getTitle() != null && item.getTitle().toLowerCase().contains(kw))
                    || (item.getDescription() != null && item.getDescription().toLowerCase().contains(kw));

            boolean matchesLoc = loc.isEmpty()
                    || (item.getLocation() != null && item.getLocation().toLowerCase().contains(loc));

            boolean matchesDate = dt.isEmpty()
                    || (item.getItemDate() != null && item.getItemDate().equalsIgnoreCase(dt));

            boolean matchesStatus = (status == null || item.getStatus() == status);

            if (matchesKw && matchesLoc && matchesDate && matchesStatus) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private void validateCommonItemFields(String title, String description, String location, String itemDate)
            throws InvalidItemException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidItemException("Item name / title cannot be empty.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidItemException("Item description cannot be empty.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new InvalidItemException("Location cannot be empty.");
        }
        if (itemDate == null || itemDate.trim().isEmpty()) {
            throw new InvalidItemException("Date cannot be empty.");
        }
    }
}
