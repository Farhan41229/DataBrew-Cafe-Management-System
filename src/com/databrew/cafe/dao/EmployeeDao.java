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
        String sql = "SELECT id, user_id, position, full_name, branch, age, status, shift_id, salary, bank_account, password_hash, hire_date FROM employees ORDER BY id";
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
        String sql = "SELECT id, user_id, position, full_name, branch, age, status, shift_id, salary, bank_account, password_hash, hire_date FROM employees WHERE id=?";
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
        String sql = "INSERT INTO employees (user_id, position, full_name, branch, age, status, shift_id, salary, bank_account, password_hash, hire_date) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setNullableLong(ps, 1, e.getUserId());
            ps.setString(2, e.getPosition());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getBranch());
            setNullableInt(ps, 5, e.getAge());
            ps.setString(6, e.getStatus());
            setNullableLong(ps, 7, e.getShiftId());
            ps.setDouble(8, e.getSalary());
            ps.setString(9, e.getBankAccount());
            ps.setString(10, e.getPasswordHash());
            ps.setDate(11, e.getHireDate() == null ? null : Date.valueOf(e.getHireDate()));
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
        String sql = "UPDATE employees SET user_id=?, position=?, full_name=?, branch=?, age=?, status=?, shift_id=?, salary=?, bank_account=?, password_hash=?, hire_date=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            setNullableLong(ps, 1, e.getUserId());
            ps.setString(2, e.getPosition());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getBranch());
            setNullableInt(ps, 5, e.getAge());
            ps.setString(6, e.getStatus());
            setNullableLong(ps, 7, e.getShiftId());
            ps.setDouble(8, e.getSalary());
            ps.setString(9, e.getBankAccount());
            ps.setString(10, e.getPasswordHash());
            ps.setDate(11, e.getHireDate() == null ? null : Date.valueOf(e.getHireDate()));
            ps.setLong(12, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        long userId = rs.getLong("user_id");
        e.setUserId(rs.wasNull() ? null : userId);
        e.setPosition(rs.getString("position"));
        e.setFullName(rs.getString("full_name"));
        e.setBranch(rs.getString("branch"));
        int age = rs.getInt("age");
        e.setAge(rs.wasNull() ? null : age);
        e.setStatus(rs.getString("status"));
        long shiftId = rs.getLong("shift_id");
        e.setShiftId(rs.wasNull() ? null : shiftId);
        Date hire = rs.getDate("hire_date");
        e.setHireDate(hire == null ? null : hire.toLocalDate());
        e.setSalary(rs.getDouble("salary"));
        e.setBankAccount(rs.getString("bank_account"));
        e.setPasswordHash(rs.getString("password_hash"));
        return e;
    }

    private void setNullableLong(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) {
            ps.setNull(idx, java.sql.Types.BIGINT);
        } else {
            ps.setLong(idx, value);
        }
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(idx, java.sql.Types.INTEGER);
        } else {
            ps.setInt(idx, value);
        }
    }
}
