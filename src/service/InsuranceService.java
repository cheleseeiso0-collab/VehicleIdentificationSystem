package service;

import model.Insurance;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceService {

    public static void add(int vehicleId, String provider, String policy,
                           String start, String expiry, String coverage,
                           double premium) throws SQLException {
        String sql = "INSERT INTO insurance(vehicle_id,provider,policy_number," +
                     "start_date,expiry_date,coverage_type,premium_amount) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setString(2, provider);
            ps.setString(3, policy);
            ps.setDate(4, Date.valueOf(start));
            ps.setDate(5, Date.valueOf(expiry));
            ps.setString(6, coverage);
            ps.setBigDecimal(7, BigDecimal.valueOf(premium));
            ps.executeUpdate();
        }
    }

    public static void updateStatus(int id, String status) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "UPDATE insurance SET status=? WHERE insurance_id=?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "DELETE FROM insurance WHERE insurance_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<Insurance> getAll() throws SQLException {
        List<Insurance> list = new ArrayList<>();
        String sql = "SELECT insurance_id,vehicle_id,provider,policy_number," +
                     "start_date::text,expiry_date::text,coverage_type," +
                     "premium_amount,status FROM insurance ORDER BY expiry_date DESC";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private static Insurance map(ResultSet rs) throws SQLException {
        return new Insurance(
            rs.getInt("insurance_id"),    rs.getInt("vehicle_id"),
            rs.getString("provider"),     rs.getString("policy_number"),
            rs.getString("start_date"),   rs.getString("expiry_date"),
            rs.getString("coverage_type"),rs.getDouble("premium_amount"),
            rs.getString("status"));
    }
}
