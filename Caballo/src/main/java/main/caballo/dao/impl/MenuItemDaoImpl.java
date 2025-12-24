package main.caballo.dao.impl;

import main.caballo.dao.MenuItemDao;
import main.caballo.model.MenuItem;
import main.caballo.util.DbUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDaoImpl implements MenuItemDao {

    @Override
    public MenuItem create(MenuItem item) {
        String sql = "INSERT INTO menu_items(naziv, opis, cijena, kategorija, current_qty) VALUES(?,?,?,?,?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getCategory());
            ps.setInt(5, item.getCurrentQty());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) item.setId(keys.getLong(1));
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("MenuItem create failed", e);
        }
    }

    @Override
    public boolean update(MenuItem item) {
        String sql = "UPDATE menu_items SET naziv=?, opis=?, cijena=?, kategorija=?, current_qty=? WHERE id=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getCategory());
            ps.setInt(5, item.getCurrentQty());
            ps.setLong(6, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("MenuItem update failed", e);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "UPDATE menu_items SET is_active = 0 WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("MenuItem delete failed", e);
        }
    }

    @Override
    public List<MenuItem> findAll() {
        String sql = "SELECT id, naziv, opis, cijena, kategorija, current_qty, is_active  FROM menu_items WHERE is_active = 1 ORDER BY kategorija, naziv";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<MenuItem> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("MenuItem findAll failed", e);
        }
    }

    @Override
    public List<MenuItem> search(String query, String category) {
        StringBuilder sb = new StringBuilder("SELECT id, naziv, opis, cijena, kategorija, current_qty, is_active FROM menu_items WHERE is_active = 1 ");
        List<Object> params = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            sb.append("AND LOWER(naziv) LIKE ? ");
            params.add("%" + query.toLowerCase() + "%");
        }
        if (category != null && !category.isBlank()) {
            sb.append("AND kategorija = ? ");
            params.add(category);
        }
        sb.append("ORDER BY kategorija, naziv");

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<MenuItem> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("MenuItem search failed", e);
        }
    }

    private MenuItem map(ResultSet rs) throws SQLException {
        return new MenuItem(
                rs.getLong("id"),
                rs.getString("naziv"),
                rs.getString("opis"),
                rs.getDouble("cijena"),
                rs.getString("kategorija"),
                rs.getInt("current_qty"),
                rs.getBoolean("is_active")
        );
    }

    @Override
    public void addDelivery(long itemId, int quantity) {
        LocalDate today = LocalDate.now();
        String ensure = "CALL ensure_item_stock_for_date(?)";
        String update = "UPDATE menu_items SET current_qty = current_qty + ? WHERE id = ?";

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(ensure);
                 PreparedStatement ps2 = c.prepareStatement(update)) {
                ps1.setDate(1, Date.valueOf(today));
                ps1.execute();

                ps2.setInt(1, quantity);
                ps2.setLong(2, itemId);
                ps2.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("MenuItem addDelivery failed", e);
        }
    }
}

