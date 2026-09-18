package campus.lostfound.model;

import campus.lostfound.enums.UserType;

public class Staff extends User {
    private String staffId;
    private String designation;

    public Staff() {
        super();
        setUserType(UserType.STAFF);
    }

    public Staff(int userId, String name, String email, String password, String phone,
                 String department, String staffId, String designation) {
        super(userId, name, email, password, phone, UserType.STAFF, department);
        this.staffId = staffId;
        this.designation = designation;
    }

    @Override
    public String getRoleDetails() {
        return "Staff ID: " + staffId + " | Designation: " + (designation != null ? designation : "Campus Staff");
    }

    @Override
    public String getIdentifier() {
        return staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
