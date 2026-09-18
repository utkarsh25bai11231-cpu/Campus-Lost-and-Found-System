package campus.lostfound.model;

import campus.lostfound.enums.UserType;

public class Faculty extends User {
    private String employeeId;
    private String cabinNumber;

    public Faculty() {
        super();
        setUserType(UserType.FACULTY);
    }

    public Faculty(int userId, String name, String email, String password, String phone,
                   String department, String employeeId, String cabinNumber) {
        super(userId, name, email, password, phone, UserType.FACULTY, department);
        this.employeeId = employeeId;
        this.cabinNumber = cabinNumber;
    }

    @Override
    public String getRoleDetails() {
        return "Employee ID: " + employeeId + " | Cabin: " + (cabinNumber != null ? cabinNumber : "N/A");
    }

    @Override
    public String getIdentifier() {
        return employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCabinNumber() {
        return cabinNumber;
    }

    public void setCabinNumber(String cabinNumber) {
        this.cabinNumber = cabinNumber;
    }
}
