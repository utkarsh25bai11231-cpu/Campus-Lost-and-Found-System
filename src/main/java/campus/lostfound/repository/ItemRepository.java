package campus.lostfound.repository;

import campus.lostfound.database.DatabaseConnection;
import campus.lostfound.enums.ItemCategory;
import campus.lostfound.enums.ItemStatus;
import campus.lostfound.model.FoundReport;
import campus.lostfound.model.Item;
import campus.lostfound.model.LostReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRepository {
    // In-memory cache / fallback store
    private static final List<Item> memoryItems = new ArrayList<>();
    private static final Map<Integer, Item> idIndex = new HashMap<>();
    private static final Map<ItemCategory, List<Item>> categoryIndex = new HashMap<>();
    private static int nextId = 1;

    static {
        // Seed sample items
        LostReport lost1 = new LostReport(1, "Blue HP Laptop Bag with Charger",
                "Navy blue HP 15.6 inch bag containing original 65W charger and wireless mouse",
                ItemCategory.BAGS, "Central Library Ground Floor", "2026-09-15",
                ItemStatus.OPEN, 1, 500.0, true);
        lost1.setReporterName("Rahul Sharma");

        FoundReport found1 = new FoundReport(2, "Navy Blue HP Laptop Bag",
                "Found a blue laptop bag near library study cubicle, contains mouse and adapter",
                ItemCategory.BAGS, "Central Library Ground Floor", "2026-09-15",
                ItemStatus.OPEN, 2, "Security Office AB-1");
        found1.setReporterName("Dr. Ananya Verma");

        LostReport lost2 = new LostReport(3, "VIT Student ID Card",
                "Registration number 23BCE10123 card in red VIT lanyard",
                ItemCategory.ID_CARD, "Food Street Near Nescafe", "2026-09-16",
                ItemStatus.OPEN, 1, 100.0, true);
        lost2.setReporterName("Rahul Sharma");

        FoundReport found2 = new FoundReport(4, "Red Lanyard Student ID Card",
                "ID Card found on bench outside food street cafeteria",
                ItemCategory.ID_CARD, "Food Street Cafeteria", "2026-09-16",
                ItemStatus.OPEN, 3, "Campus Security Main Gate");
        found2.setReporterName("Ramesh Kumar");

        memoryItems.add(lost1);
        memoryItems.add(found1);
        memoryItems.add(lost2);
        memoryItems.add(found2);
        nextId = 5;

        rebuildIndexes();
    }

    private static synchronized void rebuildIndexes() {
        idIndex.clear();
        categoryIndex.clear();

        for (Item item : memoryItems) {
            idIndex.put(item.getItemId(), item);

            List<Item> catList = categoryIndex.get(item.getCategory());
            if (catList == null) {
                catList = new ArrayList<>();
                categoryIndex.put(item.getCategory(), catList);
            }
            catList.add(item);
        }
    }

    public Item save(Item item) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO items (report_type, title, description, category, location, item_date, status, user_id, reward_amount, is_identifiable, storage_location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, item.getReportType());
                ps.setString(2, item.getTitle());
                ps.setString(3, item.getDescription());
                ps.setString(4, item.getCategory().name());
                ps.setString(5, item.getLocation());
                ps.setString(6, item.getItemDate());
                ps.setString(7, item.getStatus().name());
                ps.setInt(8, item.getUserId());

                if (item instanceof LostReport) {
                    LostReport lr = (LostReport) item;
                    ps.setDouble(9, lr.getRewardAmount());
                    ps.setBoolean(10, lr.isIdentifiable());
                    ps.setNull(11, Types.VARCHAR);
                } else if (item instanceof FoundReport) {
                    FoundReport fr = (FoundReport) item;
                    ps.setDouble(9, 0.0);
                    ps.setBoolean(10, false);
                    ps.setString(11, fr.getStorageLocation());
                }

                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    item.setItemId(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("[ItemRepository Error] Database save failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        if (item.getItemId() == 0) {
            item.setItemId(nextId++);
        }
        memoryItems.add(item);
        rebuildIndexes();
        return item;
    }

    public boolean updateItem(int itemId, String title, String description, String location) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE items SET title = ?, description = ?, location = ? WHERE item_id = ?";
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, location);
                ps.setInt(4, itemId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[ItemRepository Error] Database update failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        Item inMemory = idIndex.get(itemId);
        if (inMemory != null) {
            inMemory.updateDetails(title, description, location);
            rebuildIndexes();
            return true;
        }
        return false;
    }

    public boolean deleteItem(int itemId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "DELETE FROM items WHERE item_id = ?";
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, itemId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[ItemRepository Error] Database delete failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        Item toRemove = null;
        for (Item it : memoryItems) {
            if (it.getItemId() == itemId) {
                toRemove = it;
                break;
            }
        }
        if (toRemove != null) {
            memoryItems.remove(toRemove);
            rebuildIndexes();
            return true;
        }
        return false;
    }

    public void updateStatus(int itemId, ItemStatus newStatus) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE items SET status = ? WHERE item_id = ?";
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, newStatus.name());
                ps.setInt(2, itemId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[ItemRepository Error] Update status failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        Item item = idIndex.get(itemId);
        if (item != null) {
            item.setStatus(newStatus);
        }
    }

    public Item findById(int itemId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT i.*, u.name AS reporter_name FROM items i LEFT JOIN users u ON i.user_id = u.user_id WHERE i.item_id = ?";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, itemId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    return mapItemFromResultSet(rs);
                }
            } catch (SQLException e) {
                System.err.println("[ItemRepository Error] findById query failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        return idIndex.get(itemId);
    }

    public List<Item> findAll() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            List<Item> list = new ArrayList<>();
            String sql = "SELECT i.*, u.name AS reporter_name FROM items i LEFT JOIN users u ON i.user_id = u.user_id ORDER BY i.item_id DESC";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    list.add(mapItemFromResultSet(rs));
                }
                return list;
            } catch (SQLException e) {
                System.err.println("[ItemRepository Error] findAll query failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(stmt);
                DatabaseConnection.close(conn);
            }
        }
        return new ArrayList<>(memoryItems);
    }

    public List<Item> findByReportType(String reportType) {
        List<Item> all = findAll();
        List<Item> filtered = new ArrayList<>();
        for (Item it : all) {
            if (it.getReportType().equalsIgnoreCase(reportType)) {
                filtered.add(it);
            }
        }
        return filtered;
    }

    public List<Item> findByUserId(int userId) {
        List<Item> all = findAll();
        List<Item> filtered = new ArrayList<>();
        for (Item it : all) {
            if (it.getUserId() == userId) {
                filtered.add(it);
            }
        }
        return filtered;
    }

    // Fast HashMap index lookup demonstrating Java Collections indexing
    public List<Item> findByCategoryFromIndex(ItemCategory category) {
        List<Item> indexed = categoryIndex.get(category);
        if (indexed != null) {
            return new ArrayList<>(indexed);
        }
        return new ArrayList<>();
    }

    private Item mapItemFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("item_id");
        String type = rs.getString("report_type");
        String title = rs.getString("title");
        String desc = rs.getString("description");
        ItemCategory category = ItemCategory.fromString(rs.getString("category"));
        String location = rs.getString("location");
        String itemDate = rs.getString("item_date");
        ItemStatus status = ItemStatus.fromString(rs.getString("status"));
        int userId = rs.getInt("user_id");
        String reporterName = rs.getString("reporter_name");

        if ("LOST".equalsIgnoreCase(type)) {
            double reward = rs.getDouble("reward_amount");
            boolean identifiable = rs.getBoolean("is_identifiable");
            LostReport lr = new LostReport(id, title, desc, category, location, itemDate, status, userId, reward, identifiable);
            lr.setReporterName(reporterName);
            return lr;
        } else {
            String storage = rs.getString("storage_location");
            FoundReport fr = new FoundReport(id, title, desc, category, location, itemDate, status, userId, storage);
            fr.setReporterName(reporterName);
            return fr;
        }
    }
}
