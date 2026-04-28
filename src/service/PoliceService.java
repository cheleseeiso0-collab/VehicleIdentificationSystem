package service;

import model.PoliceReport;
import model.Violation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoliceService {

    public static void addReport(int vehicleId, String date, String type,
                                 String desc, String officer) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "INSERT INTO police_report(vehicle_id,report_date,report_type," +
                "description,officer_name) VALUES(?,?,?,?,?)")) {
            ps.setInt(1,vehicleId); ps.setDate(2,Date.valueOf(date));
            ps.setString(3,type);   ps.setString(4,desc); ps.setString(5,officer);
            ps.executeUpdate();
        }
    }

    public static List<PoliceReport> getAllReports() throws SQLException {
        List<PoliceReport> list = new ArrayList<>();
        String sql = "SELECT report_id,vehicle_id,report_date::text,report_type," +
                     "description,officer_name FROM police_report ORDER BY report_date DESC";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new PoliceReport(rs.getInt("report_id"),rs.getInt("vehicle_id"),
                    rs.getString("report_date"),rs.getString("report_type"),
                    rs.getString("description"),rs.getString("officer_name")));
        }
        return list;
    }

    public static void addViolation(int vehicleId, String date,
                                    String type, double fine) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "INSERT INTO violation(vehicle_id,violation_date,violation_type,fine_amount) " +
                "VALUES(?,?,?,?)")) {
            ps.setInt(1,vehicleId); ps.setDate(2,Date.valueOf(date));
            ps.setString(3,type);   ps.setDouble(4,fine);
            ps.executeUpdate();
        }
    }

    public static void payViolation(int id) throws SQLException {
        try (CallableStatement cs = DBConnection.get().prepareCall(
                "CALL sp_pay_violation(?)")) {
            cs.setInt(1,id); cs.execute();
        }
    }

    public static List<Violation> getAllViolations() throws SQLException {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT violation_id,vehicle_id,violation_date::text,violation_type," +
                     "fine_amount,status FROM violation ORDER BY violation_date DESC";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Violation(rs.getInt("violation_id"),rs.getInt("vehicle_id"),
                    rs.getString("violation_date"),rs.getString("violation_type"),
                    rs.getDouble("fine_amount"),rs.getString("status")));
        }
        return list;
    }
}
