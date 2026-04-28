package service;

import model.ServiceRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkshopService {

    public static void addService(int vehicleId, String date, String type,
                                  String desc, double cost) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "INSERT INTO service_record(vehicle_id,service_date,service_type,description,cost) " +
                "VALUES(?,?,?,?,?)")) {
            ps.setInt(1,vehicleId); ps.setDate(2, Date.valueOf(date));
            ps.setString(3,type);   ps.setString(4,desc); ps.setDouble(5,cost);
            ps.executeUpdate();
        }
    }

    public static void deleteService(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "DELETE FROM service_record WHERE service_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }

    public static List<ServiceRecord> getAll() throws SQLException {
        List<ServiceRecord> list = new ArrayList<>();
        String sql = "SELECT service_id,vehicle_id,service_date::text,service_type," +
                     "description,cost FROM service_record ORDER BY service_date DESC";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new ServiceRecord(rs.getInt("service_id"),
                    rs.getInt("vehicle_id"), rs.getString("service_date"),
                    rs.getString("service_type"), rs.getString("description"),
                    rs.getDouble("cost")));
        }
        return list;
    }
}
