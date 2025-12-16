package com.databrew.cafe.dao;

import com.databrew.cafe.model.Shift;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShiftDao {

    public List<Shift> findAll() throws SQLException {
        String sql = "SELECT id, name, start_time, end_time FROM shifts ORDER BY start_time";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Shift> list = new ArrayList<>();
            while (rs.next()) {
                Shift s = new Shift();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setStartTime(rs.getString("start_time"));
                s.setEndTime(rs.getString("end_time"));
                list.add(s);
            }
            return list;
        }
    }
}
