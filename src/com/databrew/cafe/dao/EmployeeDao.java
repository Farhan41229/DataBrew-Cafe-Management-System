package com.databrew.cafe.dao;

import com.databrew.cafe.model.Employee;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT id, user_id, position, hire_date, salary FROM employees ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Employee> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        }
    }

    public Employee findById(long id) throws SQLException {
        String sql = "SELECT id, user_id, position, hire_date, salary FROM employees WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }
        }
    }

    public long insert(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (user_id, position, hire_date, salary) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (e.getUserId() == null) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, e.getUserId());
            }
            ps.setString(2, e.getPosition());
            ps.setDate(3, Date.valueOf(e.getHireDate()));
            ps.setDouble(4, e.getSalary());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Insert employee failed");
    }

    public void update(Employee e) throws SQLException {
        String sql = "UPDATE employees SET user_id=?, position=?, hire_date=?, salary=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (e.getUserId() == null) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, e.getUserId());
            }
            ps.setString(2, e.getPosition());
            ps.setDate(3, Date.valueOf(e.getHireDate()));
            ps.setDouble(4, e.getSalary());
            ps.setLong(5, e.getId());
            ps.executeUpdate();
        }
    }

    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        long userId = rs.getLong("user_id");
        e.setUserId(rs.wasNull() ? null : userId);
        e.setPosition(rs.getString("position"));
        e.setHireDate(rs.getDate("hire_date").toLocalDate());
        e.setSalary(rs.getDouble("salary"));
        return e;
    }
}
