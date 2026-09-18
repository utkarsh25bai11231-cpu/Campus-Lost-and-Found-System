package campus.lostfound.enums;

public enum ItemCategory {
    ELECTRONICS,
    ID_CARD,
    BOOKS_STATIONERY,
    BAGS,
    KEYS,
    CLOTHING,
    BOTTLE,
    OTHER;

    public static ItemCategory fromString(String value) {
        if (value == null) return OTHER;
        try {
            return ItemCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
