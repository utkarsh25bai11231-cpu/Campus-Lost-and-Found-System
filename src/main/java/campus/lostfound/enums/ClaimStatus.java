package campus.lostfound.enums;

public enum ClaimStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static ClaimStatus fromString(String value) {
        if (value == null) return PENDING;
        try {
            return ClaimStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
