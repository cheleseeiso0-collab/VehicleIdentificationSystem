package service;

import model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleService {

    public static void register(String reg, String make, String model, int year,
                                String color, String chassis, int ownerId)
            throws SQLException {
        try (CallableStatement cs = DBConnection.get().prepareCall(
                "CALL sp_register_vehicle(?,?,?,?,?,?,?)")) {
            cs.setString(1,reg); cs.setString(2,make); cs.setString(3,model);
            cs.setInt(4,year);   cs.setString(5,color); cs.setString(6,chassis);
            cs.setInt(7,ownerId);
            cs.execute();
        }
    }

    public static void update(int id, String reg, String make, String model,
                              int year, String color, String chassis, int ownerId)
            throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "UPDATE vehicle SET registration_number=?,make=?,model=?,year=?," +
                "color=?,chassis_number=?,owner_id=? WHERE vehicle_id=?")) {
            ps.setString(1,reg); ps.setString(2,make); ps.setString(3,model);
            ps.setInt(4,year);   ps.setString(5,color); ps.setString(6,chassis);
            ps.setInt(7,ownerId); ps.setInt(8,id);
            ps.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "DELETE FROM vehicle WHERE vehicle_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }

    public static List<Vehicle> getAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT vehicle_id,registration_number,make,model,year," +
                     "color,chassis_number,owner_id,owner_name " +
                     "FROM vw_vehicle_details ORDER BY registration_number";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public static Vehicle findByReg(String reg) throws SQLException {
        String sql = "SELECT vehicle_id,registration_number,make,model,year," +
                     "color,chassis_number,owner_id,owner_name " +
                     "FROM vw_vehicle_details WHERE registration_number ILIKE ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, reg.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    private static Vehicle map(ResultSet rs) throws SQLException {
        return new Vehicle(rs.getInt("vehicle_id"),
            rs.getString("registration_number"), rs.getString("make"),
            rs.getString("model"), rs.getInt("year"),
            rs.getString("color"), rs.getString("chassis_number"),
            rs.getInt("owner_id"), rs.getString("owner_name"));
    }
}
