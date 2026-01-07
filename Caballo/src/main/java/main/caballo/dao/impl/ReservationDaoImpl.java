package main.caballo.dao.impl;

import main.caballo.dao.ReservationDao;
import main.caballo.model.Reservation;
import main.caballo.util.DbUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDaoImpl implements ReservationDao {
    @Override
    public Reservation create(Reservation r) {
        String seatsSql = "SELECT broj_sjedista FROM tables WHERE id=?";

        String overlapSql = """
            SELECT 1
            FROM reservations
            WHERE table_id=?
              AND datum_rezervacije=?
              AND ? >= SUBTIME(vrijeme_dolaska, '02:00:00')
              AND ? <  ADDTIME(vrijeme_dolaska, '02:00:00')
            LIMIT 1
            """;

        String insertSql = "INSERT INTO reservations(table_id, ime_gosta, broj_telefona, datum_rezervacije, vrijeme_dolaska, broj_osoba, napomena) VALUES(?,?,?,?,?,?,?)";

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                int seats;
                try (PreparedStatement ps = c.prepareStatement(seatsSql)) {
                    ps.setLong(1, r.getTableId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Table not found.");
                        seats = rs.getInt(1);
                    }
                }
                if (r.getBrojOsoba() > seats) {
                    throw new IllegalArgumentException("People count exceeds table seats (" + seats + ").");
                }

                LocalTime desired = r.getVrijemeDolaska().withSecond(0).withNano(0);

                try (PreparedStatement ps = c.prepareStatement(overlapSql)) {
                    ps.setLong(1, r.getTableId());
                    ps.setDate(2, Date.valueOf(r.getDatumRezervacije()));
                    ps.setTime(3, Time.valueOf(desired));
                    ps.setTime(4, Time.valueOf(desired));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new IllegalArgumentException("Table is already reserved within the selected time window.");
                        }
                    }
                }

                try (PreparedStatement ps = c.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, r.getTableId());
                    ps.setString(2, r.getImeGosta());
                    ps.setString(3, r.getBrojTelefona());
                    ps.setDate(4, Date.valueOf(r.getDatumRezervacije()));
                    ps.setTime(5, Time.valueOf(desired));
                    ps.setInt(6, r.getBrojOsoba());
                    ps.setString(7, r.getNapomena());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) r.setId(keys.getLong(1));
                    }
                }

                c.commit();
                return r;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("create reservation failed", e);
        }
    }

    @Override
    public boolean update(Reservation r) {
        String seatsSql = "SELECT broj_sjedista FROM tables WHERE id=?";

        String overlapSql = """
            SELECT 1
            FROM reservations
            WHERE table_id=?
              AND datum_rezervacije=?
              AND id <> ?
              AND ? >= SUBTIME(vrijeme_dolaska, '02:00:00')
              AND ? <  ADDTIME(vrijeme_dolaska, '02:00:00')
            LIMIT 1
            """;

        String updateSql = "UPDATE reservations SET table_id=?, ime_gosta=?, broj_telefona=?, datum_rezervacije=?, vrijeme_dolaska=?, broj_osoba=?, napomena=? WHERE id=?";

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                int seats;
                try (PreparedStatement ps = c.prepareStatement(seatsSql)) {
                    ps.setLong(1, r.getTableId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Table not found.");
                        seats = rs.getInt(1);
                    }
                }
                if (r.getBrojOsoba() > seats) {
                    throw new IllegalArgumentException("People count exceeds table seats (" + seats + ").");
                }

                LocalTime desired = r.getVrijemeDolaska().withSecond(0).withNano(0);

                try (PreparedStatement ps = c.prepareStatement(overlapSql)) {
                    ps.setLong(1, r.getTableId());
                    ps.setDate(2, Date.valueOf(r.getDatumRezervacije()));
                    ps.setLong(3, r.getId());
                    ps.setTime(4, Time.valueOf(desired));
                    ps.setTime(5, Time.valueOf(desired));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new IllegalArgumentException("Table is already reserved within the selected time window.");
                        }
                    }
                }

                boolean updated;
                try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                    ps.setLong(1, r.getTableId());
                    ps.setString(2, r.getImeGosta());
                    ps.setString(3, r.getBrojTelefona());
                    ps.setDate(4, Date.valueOf(r.getDatumRezervacije()));
                    ps.setTime(5, Time.valueOf(desired));
                    ps.setInt(6, r.getBrojOsoba());
                    ps.setString(7, r.getNapomena());
                    ps.setLong(8, r.getId());
                    updated = ps.executeUpdate() > 0;
                }

                c.commit();
                return updated;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("update reservation failed", e);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM reservations WHERE id=?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("delete reservation failed", e);
        }
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        String sql = "SELECT id, table_id, ime_gosta, broj_telefona, datum_rezervacije, vrijeme_dolaska, broj_osoba, napomena FROM reservations WHERE datum_rezervacije=? ORDER BY vrijeme_dolaska";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                List<Reservation> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("findByDate reservation failed", e);
        }
    }

    private Reservation map(ResultSet rs) throws Exception {
        return new Reservation(
                rs.getLong("id"),
                rs.getLong("table_id"),
                rs.getString("ime_gosta"),
                rs.getString("broj_telefona"),
                rs.getDate("datum_rezervacije").toLocalDate(),
                rs.getTime("vrijeme_dolaska").toLocalTime(),
                rs.getInt("broj_osoba"),
                rs.getString("napomena")
        );
    }
}
