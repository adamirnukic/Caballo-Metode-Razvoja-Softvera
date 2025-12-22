package main.caballo.dao.impl;

import main.caballo.dao.ReportDao;
import main.caballo.model.DailyItemReport;
import main.caballo.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class ReportDaoImpl implements ReportDao {
    @Override
    public double totalForDate(LocalDate date) {
        String sql = "SELECT COALESCE(SUM(ukupno),0) FROM orders WHERE DATE(datum)=?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("totalForDate failed", e);
        }
    }

    @Override
    public List<Map.Entry<String, Integer>> topItemsForDate(LocalDate date, int limit) {
        String sql = "SELECT mi.naziv, SUM(oi.kolicina) AS qty FROM order_items oi JOIN orders o ON o.id=oi.order_id JOIN menu_items mi ON mi.id=oi.item_id WHERE DATE(o.datum)=? AND mi.kategorija <> 'Piće' GROUP BY mi.naziv ORDER BY qty DESC LIMIT ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map.Entry<String, Integer>> list = new ArrayList<>();
                while (rs.next()) list.add(new AbstractMap.SimpleEntry<>(rs.getString(1), rs.getInt(2)));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("topItemsForDate failed", e);
        }
    }

    @Override
    public List<Map.Entry<String, Double>> salesByWaiter(LocalDate date) {
        String sql = "SELECT u.username, COALESCE(SUM(o.ukupno),0) AS total FROM users u LEFT JOIN orders o ON o.user_id=u.id AND DATE(o.datum)=? GROUP BY u.username ORDER BY total DESC";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                List<Map.Entry<String, Double>> list = new ArrayList<>();
                while (rs.next()) list.add(new AbstractMap.SimpleEntry<>(rs.getString(1), rs.getDouble(2)));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("salesByWaiter failed", e);
        }
    }

    @Override
    public List<DailyItemReport> dailyItemReport(LocalDate date) {
        String sql = """
        SELECT
            mi.naziv,
            COALESCE(sm.opening_qty, 0) AS opening_qty,
            COALESCE(sm.received_qty, 0) AS received_qty,
            COALESCE(SUM(oi.kolicina), 0) AS sold_qty,
            (COALESCE(sm.opening_qty, 0) + COALESCE(sm.received_qty, 0) - COALESCE(SUM(oi.kolicina), 0)) AS expected_closing_qty,
            COALESCE(sm.physical_closing_qty, 0) AS physical_closing_qty,
            COALESCE(SUM(oi.kolicina) * mi.cijena, 0) AS sold_total_amount,
            sm.napomena AS note
        FROM menu_items mi
        LEFT JOIN item_stock_movements sm
            ON sm.item_id = mi.id
            AND sm.datum = ?
        LEFT JOIN orders o
            ON o.datum >= ?
            AND o.datum < DATE_ADD(?, INTERVAL 1 DAY)
        LEFT JOIN order_items oi
            ON oi.order_id = o.id
            AND oi.item_id = mi.id
        WHERE mi.kategorija = 'Piće'
        GROUP BY
            mi.id,
            mi.naziv,
            sm.opening_qty,
            sm.received_qty,
            sm.physical_closing_qty,
            sm.napomena,
            mi.cijena
        ORDER BY mi.naziv
        """;

        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            ps.setDate(1, sqlDate);
            ps.setDate(2, sqlDate);
            ps.setDate(3, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                List<DailyItemReport> list = new ArrayList<>();
                while (rs.next()) {
                    DailyItemReport r = new DailyItemReport();
                    r.setItemName(rs.getString("naziv"));
                    r.setOpeningQty(rs.getInt("opening_qty"));
                    r.setReceivedQty(rs.getInt("received_qty"));
                    r.setSoldQty(rs.getInt("sold_qty"));
                    r.setExpectedClosingQty(rs.getInt("expected_closing_qty"));
                    r.setPhysicalClosingQty(rs.getInt("physical_closing_qty"));
                    r.setSoldTotalAmount(rs.getDouble("sold_total_amount"));
                    r.setNote(rs.getString("note"));
                    list.add(r);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("dailyItemReport failed", e);
        }
    }

    @Override
    public void ensureItemStockForDate(LocalDate date) {
        String sql = "CALL ensure_item_stock_for_date(?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("ensureItemStockForDate failed for date " + date, e);
        }
    }

    @Override
    public void closeDayStock(LocalDate date) {
        String sql = "CALL close_day_stock(?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("closeDayStock failed for date " + date, e);
        }
    }

    @Override
    public void addReceivedQty(LocalDate date, long itemId, int quantity) {
        String selectSql = """
        SELECT id, received_qty
        FROM item_stock_movements
        WHERE datum = ? AND item_id = ?
        """;

        String updateSql = """
        UPDATE item_stock_movements
        SET received_qty = received_qty + ?
        WHERE id = ?
        """;

        String insertSql = """
        INSERT INTO item_stock_movements (datum, item_id, opening_qty, received_qty, physical_closing_qty)
        VALUES (?, ?, 0, ?, 0)
        """;

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psSel = c.prepareStatement(selectSql)) {
                psSel.setDate(1, java.sql.Date.valueOf(date));
                psSel.setLong(2, itemId);
                try (ResultSet rs = psSel.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        try (PreparedStatement psUpd = c.prepareStatement(updateSql)) {
                            psUpd.setInt(1, quantity);
                            psUpd.setLong(2, id);
                            psUpd.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement psIns = c.prepareStatement(insertSql)) {
                            psIns.setDate(1, java.sql.Date.valueOf(date));
                            psIns.setLong(2, itemId);
                            psIns.setInt(3, quantity);
                            psIns.executeUpdate();
                        }
                    }
                }
            }
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("addReceivedQty failed for date " + date + ", item " + itemId, e);
        }
    }

    @Override
    public void updatePhysicalQtyAndNote(LocalDate date, String itemName, int physicalQty, String note) {
        String updateStockSql = """
        UPDATE item_stock_movements sm
        JOIN menu_items mi ON mi.id = sm.item_id
        SET sm.physical_closing_qty = ?, sm.napomena = ?
        WHERE sm.datum = ? AND mi.naziv = ?
        """;

        String updateMenuSql = """
        UPDATE menu_items
        SET current_qty = ?
        WHERE naziv = ?
        """;

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psStock = c.prepareStatement(updateStockSql);
                 PreparedStatement psMenu = c.prepareStatement(updateMenuSql)) {

                psStock.setInt(1, physicalQty);
                psStock.setString(2, note);
                psStock.setDate(3, java.sql.Date.valueOf(date));
                psStock.setString(4, itemName);
                psStock.executeUpdate();

                psMenu.setInt(1, physicalQty);
                psMenu.setString(2, itemName);
                psMenu.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw new RuntimeException("updatePhysicalQtyAndNote failed for date " + date + ", itemName " + itemName, e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("updatePhysicalQtyAndNote failed for date " + date + ", itemName " + itemName, e);
        }
    }
}