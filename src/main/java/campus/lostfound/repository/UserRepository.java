package campus.lostfound.repository;

import campus.lostfound.database.DatabaseConnection;
import campus.lostfound.enums.UserType;
import campus.lostfound.model.Faculty;
import campus.lostfound.model.Staff;
import campus.lostfound.model.Student;
import campus.lostfound.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    // In-memory cache / fallback store
    private static final List<User> memoryUsers = new ArrayList<>();
    private static final Map<String, User> emailIndex = new HashMap<>();
    private static final Map<Integer, User> idIndex = new HashMap<>();
    private static int nextId = 1;

    static {
        // Seed default sample accounts for instant testing
        Student student = new Student(1, "Rahul Sharma", "rahul@vitbhopal.ac.in", "pass123", "9876543210",
                "SCOPE", "23BCE10123", "Block 1 - Room 204");
        Faculty faculty = new Faculty(2, "Dr. Ananya Verma", "ananya@vitbhopal.ac.in", "prof123", "9811223344",
                "SCSE", "FAC2045", "Cabin AB-1 305");
        Staff staff = new Staff(3, "Ramesh Kumar", "ramesh@vitbhopal.ac.in", "staff123", "9711002233",
                "Campus Security", "STF102", "Security Head");

        memoryUsers.add(student);
        memoryUsers.add(faculty);
        memoryUsers.add(staff);
        nextId = 4;

        rebuildIndexes();
    }

    private static synchronized void rebuildIndexes() {
        emailIndex.clear();
        idIndex.clear();
        for (User u : memoryUsers) {
            emailIndex.put(u.getEmail().toLowerCase(), u);
            idIndex.put(u.getUserId(), u);
        }
    }

    public User save(User user) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO users (name, email, password, phone, user_type, reg_no, employee_id, staff_id, department, extra_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getPhone());
                ps.setString(5, user.getUserType().name());

                if (user instanceof Student) {
                    Student s = (Student) user;
                    ps.setString(6, s.getRegNo());
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.VARCHAR);
                    ps.setString(9, s.getDepartment());
                    ps.setString(10, s.getHostelBlockRoom());
                } else if (user instanceof Faculty) {
                    Faculty f = (Faculty) user;
                    ps.setNull(6, Types.VARCHAR);
                    ps.setString(7, f.getEmployeeId());
                    ps.setNull(8, Types.VARCHAR);
                    ps.setString(9, f.getDepartment());
                    ps.setString(10, f.getCabinNumber());
                } else if (user instanceof Staff) {
                    Staff st = (Staff) user;
                    ps.setNull(6, Types.VARCHAR);
                    ps.setNull(7, Types.VARCHAR);
                    ps.setString(8, st.getStaffId());
                    ps.setString(9, st.getDepartment());
                    ps.setString(10, st.getDesignation());
                }

                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("[UserRepository Error] Database save failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        if (user.getUserId() == 0) {
            user.setUserId(nextId++);
        }
        memoryUsers.add(user);
        rebuildIndexes();
        return user;
    }

    public User findByEmail(String email) {
        if (email == null) return null;

        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, email.trim());
                rs = ps.executeQuery();
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            } catch (SQLException e) {
                System.err.println("[UserRepository Error] findByEmail query failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        // Fast HashMap lookup
        return emailIndex.get(email.trim().toLowerCase());
    }

    public User findById(int userId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            } catch (SQLException e) {
                System.err.println("[UserRepository Error] findById query failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        // Fast HashMap lookup
        return idIndex.get(userId);
    }

    public List<User> findAll() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            List<User> list = new ArrayList<>();
            String sql = "SELECT * FROM users";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    list.add(mapUserFromResultSet(rs));
                }
                return list;
            } catch (SQLException e) {
                System.err.println("[UserRepository Error] findAll query failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(stmt);
                DatabaseConnection.close(conn);
            }
        }
        return new ArrayList<>(memoryUsers);
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String pass = rs.getString("password");
        String phone = rs.getString("phone");
        String typeStr = rs.getString("user_type");
        String dept = rs.getString("department");
        String extra = rs.getString("extra_info");

        UserType type = UserType.fromString(typeStr);
        if (type == UserType.STUDENT) {
            String regNo = rs.getString("reg_no");
            return new Student(id, name, email, pass, phone, dept, regNo, extra);
        } else if (type == UserType.FACULTY) {
            String empId = rs.getString("employee_id");
            return new Faculty(id, name, email, pass, phone, dept, empId, extra);
        } else {
            String staffId = rs.getString("staff_id");
            return new Staff(id, name, email, pass, phone, dept, staffId, extra);
        }
    }
}
