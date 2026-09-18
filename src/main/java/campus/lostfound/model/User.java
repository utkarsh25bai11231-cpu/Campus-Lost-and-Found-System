package campus.lostfound.model;

import campus.lostfound.enums.UserType;

public abstract class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private UserType userType;
    private String department;

    public User() {
    }

    public User(int userId, String name, String email, String password, String phone, UserType userType, String department) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.userType = userType;
        this.department = department;
    }

    // Abstract methods demonstrating polymorphism
    public abstract String getRoleDetails();
    public abstract String getIdentifier();

    public void displayProfile() {
        System.out.println("----------------------------------------");
        System.out.println("User ID       : " + userId);
        System.out.println("Name          : " + name);
        System.out.println("Email         : " + email);
        System.out.println("Phone         : " + phone);
        System.out.println("Role          : " + userType);
        System.out.println("Department    : " + (department != null ? department : "N/A"));
        System.out.println("Role Details  : " + getRoleDetails());
        System.out.println("----------------------------------------");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
