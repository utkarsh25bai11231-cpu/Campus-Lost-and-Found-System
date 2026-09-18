package campus.lostfound.enums;

public enum UserType {
    STUDENT,
    FACULTY,
    STAFF;

    public static UserType fromString(String value) {
        if (value == null) return STUDENT;
        try {
            return UserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}
