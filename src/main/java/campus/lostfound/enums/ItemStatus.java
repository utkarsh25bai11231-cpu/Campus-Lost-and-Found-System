package campus.lostfound.enums;

public enum ItemStatus {
    OPEN,
    CLAIM_PENDING,
    CLAIMED,
    CLOSED;

    public static ItemStatus fromString(String value) {
        if (value == null) return OPEN;
        try {
            return ItemStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OPEN;
        }
    }
}
