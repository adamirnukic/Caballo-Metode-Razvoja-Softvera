package main.caballo.dao.impl;

import main.caballo.dao.ShiftDao;
import main.caballo.model.Shift;
import main.caballo.model.ShiftType;
import main.caballo.util.DbUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class ShiftDaoImpl implements ShiftDao {

    @Override
    public Optional<Shift> findByUserAndDate(long userId, LocalDate date) {
        String sql = "SELECT id, user_id, datum, tip_smjene, napomena FROM shifts WHERE user_id = ? AND datum = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Shift findByUserAndDate failed", e);
        }
    }

    @Override
    public Optional<Shift> findLastBefore(long userId, LocalDate date) {
        String sql = "SELECT id, user_id, datum, tip_smjene, napomena " +
                "FROM shifts WHERE user_id = ? AND datum < ? ORDER BY datum DESC LIMIT 1";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Shift findLastBefore failed", e);
        }
    }

    @Override
    public Shift upsert(long userId, LocalDate date, ShiftType type, String note) {
        String sql = "INSERT INTO shifts(user_id, datum, tip_smjene, napomena) VALUES(?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE tip_smjene = VALUES(tip_smjene), napomena = VALUES(napomena)";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, type == null ? null : type.name().toLowerCase());
            ps.setString(4, note);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Shift(keys.getLong(1), userId, date, type, note);
                }
            }

            return findByUserAndDate(userId, date)
                    .orElseThrow(() -> new SQLException("Upsert succeeded but shift row not found."));
        } catch (SQLException e) {
            throw new RuntimeException("Shift upsert failed", e);
        }
    }

    private Shift map(ResultSet rs) throws SQLException {
        Shift s = new Shift();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        Date d = rs.getDate("datum");
        s.setDate(d == null ? null : d.toLocalDate());

        String dbType = rs.getString("tip_smjene");
        s.setType(dbType == null ? null : ShiftType.valueOf(dbType.trim().toUpperCase()));

        s.setNote(rs.getString("napomena"));
        return s;
    }
}
