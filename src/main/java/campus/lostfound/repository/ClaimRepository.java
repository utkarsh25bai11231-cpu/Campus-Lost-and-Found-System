package campus.lostfound.repository;

import campus.lostfound.database.DatabaseConnection;
import campus.lostfound.enums.ClaimStatus;
import campus.lostfound.model.Claim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaimRepository {
    private static final List<Claim> memoryClaims = new ArrayList<>();
    private static int nextId = 1;

    public Claim save(Claim claim) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO claims (lost_item_id, found_item_id, claimant_user_id, proof_details, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (claim.getLostItemId() > 0) {
                    ps.setInt(1, claim.getLostItemId());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setInt(2, claim.getFoundItemId());
                ps.setInt(3, claim.getClaimantUserId());
                ps.setString(4, claim.getProofDetails());
                ps.setString(5, claim.getStatus().name());

                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    claim.setClaimId(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("[ClaimRepository Error] Database save failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        if (claim.getClaimId() == 0) {
            claim.setClaimId(nextId++);
        }
        memoryClaims.add(claim);
        return claim;
    }

    public void updateStatus(int claimId, ClaimStatus status) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE claims SET status = ? WHERE claim_id = ?";
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, status.name());
                ps.setInt(2, claimId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[ClaimRepository Error] Update status failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        for (Claim c : memoryClaims) {
            if (c.getClaimId() == claimId) {
                c.setStatus(status);
                break;
            }
        }
    }

    public Claim findById(int claimId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT c.*, i.title AS item_title, u.name AS claimant_name, u.phone AS claimant_phone " +
                    "FROM claims c " +
                    "LEFT JOIN items i ON c.found_item_id = i.item_id " +
                    "LEFT JOIN users u ON c.claimant_user_id = u.user_id " +
                    "WHERE c.claim_id = ?";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, claimId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    return mapClaimFromResultSet(rs);
                }
            } catch (SQLException e) {
                System.err.println("[ClaimRepository Error] findById failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        for (Claim c : memoryClaims) {
            if (c.getClaimId() == claimId) {
                return c;
            }
        }
        return null;
    }

    public List<Claim> findAll() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            List<Claim> list = new ArrayList<>();
            String sql = "SELECT c.*, i.title AS item_title, u.name AS claimant_name, u.phone AS claimant_phone " +
                    "FROM claims c " +
                    "LEFT JOIN items i ON c.found_item_id = i.item_id " +
                    "LEFT JOIN users u ON c.claimant_user_id = u.user_id " +
                    "ORDER BY c.claim_id DESC";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    list.add(mapClaimFromResultSet(rs));
                }
                return list;
            } catch (SQLException e) {
                System.err.println("[ClaimRepository Error] findAll failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(rs);
                DatabaseConnection.close(stmt);
                DatabaseConnection.close(conn);
            }
        }
        return new ArrayList<>(memoryClaims);
    }

    public List<Claim> findByClaimantUserId(int userId) {
        List<Claim> all = findAll();
        List<Claim> filtered = new ArrayList<>();
        for (Claim c : all) {
            if (c.getClaimantUserId() == userId) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    public List<Claim> findByFoundItemId(int foundItemId) {
        List<Claim> all = findAll();
        List<Claim> filtered = new ArrayList<>();
        for (Claim c : all) {
            if (c.getFoundItemId() == foundItemId) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    public boolean hasActiveClaim(int foundItemId, int claimantUserId) {
        List<Claim> all = findAll();
        for (Claim c : all) {
            if (c.getFoundItemId() == foundItemId && c.getClaimantUserId() == claimantUserId) {
                if (c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.APPROVED) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteClaim(int claimId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "DELETE FROM claims WHERE claim_id = ?";
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, claimId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[ClaimRepository Error] deleteClaim failed: " + e.getMessage());
            } finally {
                DatabaseConnection.close(ps);
                DatabaseConnection.close(conn);
            }
        }

        Claim toRemove = null;
        for (Claim c : memoryClaims) {
            if (c.getClaimId() == claimId) {
                toRemove = c;
                break;
            }
        }
        if (toRemove != null) {
            memoryClaims.remove(toRemove);
            return true;
        }
        return false;
    }

    private Claim mapClaimFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("claim_id");
        int lostId = rs.getInt("lost_item_id");
        int foundId = rs.getInt("found_item_id");
        int claimantId = rs.getInt("claimant_user_id");
        String proof = rs.getString("proof_details");
        ClaimStatus status = ClaimStatus.fromString(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("claim_date");
        String dateStr = (ts != null) ? ts.toString() : "N/A";

        Claim c = new Claim(id, lostId, foundId, claimantId, proof, status, dateStr);
        c.setFoundItemTitle(rs.getString("item_title"));
        c.setClaimantName(rs.getString("claimant_name"));
        c.setClaimantContact(rs.getString("claimant_phone"));
        return c;
    }
}
