package campus.lostfound.model;

import campus.lostfound.enums.UserType;

public class Student extends User {
    private String regNo;
    private String hostelBlockRoom;

    public Student() {
        super();
        setUserType(UserType.STUDENT);
    }

    public Student(int userId, String name, String email, String password, String phone,
                   String department, String regNo, String hostelBlockRoom) {
        super(userId, name, email, password, phone, UserType.STUDENT, department);
        this.regNo = regNo;
        this.hostelBlockRoom = hostelBlockRoom;
    }

    @Override
    public String getRoleDetails() {
        return "Reg No: " + regNo + " | Hostel/Room: " + (hostelBlockRoom != null ? hostelBlockRoom : "Day Scholar");
    }

    @Override
    public String getIdentifier() {
        return regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getHostelBlockRoom() {
        return hostelBlockRoom;
    }

    public void setHostelBlockRoom(String hostelBlockRoom) {
        this.hostelBlockRoom = hostelBlockRoom;
    }
}
