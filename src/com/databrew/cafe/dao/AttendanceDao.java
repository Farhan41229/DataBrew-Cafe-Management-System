package com.databrew.cafe.dao;

import com.databrew.cafe.model.Attendance;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {

    public List<Attendance> findByEmployee(long employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, shift_id, work_date, check_in, check_out, status FROM attendance WHERE employee_id=? ORDER BY work_date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Attendance> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        }
    }

    public void upsertAssignment(long employeeId, long shiftId, java.time.LocalDate workDate) throws SQLException {
        String sql = "INSERT INTO attendance (employee_id, shift_id, work_date, status) VALUES (?,?,?, 'PRESENT') " +
                "ON DUPLICATE KEY UPDATE shift_id=VALUES(shift_id)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, employeeId);
            ps.setLong(2, shiftId);
            ps.setDate(3, Date.valueOf(workDate));
            ps.executeUpdate();
        }
    }

    private Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getLong("id"));
        a.setEmployeeId(rs.getLong("employee_id"));
        a.setShiftId(rs.getLong("shift_id"));
        a.setWorkDate(rs.getDate("work_date").toLocalDate());
        a.setCheckIn(rs.getTimestamp("check_in") == null ? null : rs.getTimestamp("check_in").toLocalDateTime());
        a.setCheckOut(rs.getTimestamp("check_out") == null ? null : rs.getTimestamp("check_out").toLocalDateTime());
        a.setStatus(rs.getString("status"));
        return a;
    }
}
