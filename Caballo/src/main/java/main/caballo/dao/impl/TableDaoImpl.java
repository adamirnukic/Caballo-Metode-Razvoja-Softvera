package main.caballo.dao.impl;

import main.caballo.dao.TableDao;
import main.caballo.model.DiningTable;
import main.caballo.util.DbUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TableDaoImpl implements TableDao {
    @Override
    public List<DiningTable> findAll() {
        String sql = "SELECT id, broj_stola, broj_sjedista FROM tables ORDER BY broj_stola";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<DiningTable> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new DiningTable(
                        rs.getLong("id"),
                        rs.getInt("broj_stola"),
                        rs.getInt("broj_sjedista")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll tables failed", e);
        }
    }

    @Override
    public List<DiningTable> findAvailableAt(LocalDate date, LocalTime time, java.time.Duration blockDuration) {
        if (date == null || time == null) throw new IllegalArgumentException("date/time required");

        long blockMinutes = (blockDuration == null) ? 0 : Math.max(0, blockDuration.toMinutes());
        long effectiveMinutes = (blockMinutes == 0) ? 1 : blockMinutes;

        String sql = """
            SELECT t.id, t.broj_stola, t.broj_sjedista
            FROM tables t
            WHERE NOT EXISTS (
                SELECT 1
                FROM reservations r
                WHERE r.table_id = t.id
                  AND r.datum_rezervacije = ?
                  AND (
                        ? >= SUBTIME(r.vrijeme_dolaska, SEC_TO_TIME(? * 60))
                    AND ? <  ADDTIME(r.vrijeme_dolaska, SEC_TO_TIME(? * 60))
                  )
            )
            ORDER BY t.broj_stola
            """;
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(time));
            ps.setLong(3, effectiveMinutes);
            ps.setTime(4, Time.valueOf(time));
            ps.setLong(5, effectiveMinutes);

            try (ResultSet rs = ps.executeQuery()) {
                List<DiningTable> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new DiningTable(
                            rs.getLong("id"),
                            rs.getInt("broj_stola"),
                            rs.getInt("broj_sjedista")
                    ));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAvailableAt failed", e);
        }
    }
}