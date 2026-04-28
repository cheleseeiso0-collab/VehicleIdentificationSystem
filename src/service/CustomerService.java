package service;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    public static void add(String name, String address, String phone, String email)
            throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "INSERT INTO customer(name,address,phone,email) VALUES(?,?,?,?)")) {
            ps.setString(1,name); ps.setString(2,address);
            ps.setString(3,phone); ps.setString(4,email);
            ps.executeUpdate();
        }
    }

    public static void update(int id, String name, String address,
                              String phone, String email) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "UPDATE customer SET name=?,address=?,phone=?,email=? WHERE customer_id=?")) {
            ps.setString(1,name); ps.setString(2,address);
            ps.setString(3,phone); ps.setString(4,email); ps.setInt(5,id);
            ps.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "DELETE FROM customer WHERE customer_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }

    public static List<Customer> getAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT customer_id,name,address,phone,email FROM customer ORDER BY name")) {
            while (rs.next())
                list.add(new Customer(rs.getInt("customer_id"), rs.getString("name"),
                    rs.getString("address"), rs.getString("phone"), rs.getString("email")));
        }
        return list;
    }
}
