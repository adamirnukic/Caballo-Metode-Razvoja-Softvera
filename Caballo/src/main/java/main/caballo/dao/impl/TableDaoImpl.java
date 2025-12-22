package main.caballo.dao.impl;

import main.caballo.dao.TableDao;
import main.caballo.model.DiningTable;
import main.caballo.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDaoImpl implements TableDao {
    @Override
    public List<DiningTable> findAll() {
        String sql = "SELECT id, broj_stola, broj_sjedista, status FROM tables ORDER BY broj_stola";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<DiningTable> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new DiningTable(
                        rs.getLong("id"),
                        rs.getInt("broj_stola"),
                        rs.getInt("broj_sjedista"),
                        rs.getString("status")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll tables failed", e);
        }
    }
}

