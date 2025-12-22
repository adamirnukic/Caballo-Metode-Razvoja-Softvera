package main.caballo.dao.impl;

import main.caballo.dao.ReservationDao;
import main.caballo.model.Reservation;
import main.caballo.util.DbUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDaoImpl implements ReservationDao {
    @Override
    public Reservation create(Reservation r) {
        String sql = "INSERT INTO reservations(table_id, ime_gosta, broj_telefona, datum_rezervacije, vrijeme_dolaska, broj_osoba, napomena) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getTableId());
            ps.setString(2, r.getImeGosta());
            ps.setString(3, r.getBrojTelefona());
            ps.setDate(4, Date.valueOf(r.getDatumRezervacije()));
            ps.setTime(5, Time.valueOf(r.getVrijemeDolaska()));
            ps.setInt(6, r.getBrojOsoba());
            ps.setString(7, r.getNapomena());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) r.setId(keys.getLong(1)); }
            return r;
        } catch (SQLException e) {
            throw new RuntimeException("create reservation failed", e);
        }
    }

    @Override
    public boolean update(Reservation r) {
        String sql = "UPDATE reservations SET table_id=?, ime_gosta=?, broj_telefona=?, datum_rezervacije=?, vrijeme_dolaska=?, broj_osoba=?, napomena=? WHERE id=?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, r.getTableId());
            ps.setString(2, r.getImeGosta());
            ps.setString(3, r.getBrojTelefona());
            ps.setDate(4, Date.valueOf(r.getDatumRezervacije()));
            ps.setTime(5, Time.valueOf(r.getVrijemeDolaska()));
            ps.setInt(6, r.getBrojOsoba());
            ps.setString(7, r.getNapomena());
            ps.setLong(8, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
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

