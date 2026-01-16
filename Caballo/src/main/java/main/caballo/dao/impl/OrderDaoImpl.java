package main.caballo.dao.impl;

import main.caballo.dao.OrderDao;
import main.caballo.model.Order;
import main.caballo.model.OrderItem;
import main.caballo.util.DbUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    @Override
    public Order create(Order o) {
        String sql = "INSERT INTO orders(user_id, datum, ukupno) VALUES(?,?,?)";
        if (o.getDatum() == null) o.setDatum(LocalDateTime.now());
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, o.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(o.getDatum()));
            ps.setDouble(3, o.getUkupno());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) o.setId(keys.getLong(1)); }
            return o;
        } catch (SQLException e) {
            throw new RuntimeException("create order failed", e);
        }
    }

    @Override
    public boolean addItem(OrderItem item) {
        String insert = "INSERT INTO order_items(order_id, item_id, kolicina) VALUES(?,?,?)";
        String updateTotal = "UPDATE orders SET ukupno = ukupno + (SELECT cijena FROM menu_items WHERE id=?) * ? WHERE id=?";
        String updateStock = "UPDATE menu_items SET current_qty = current_qty - ? WHERE id=? AND item_type='DRINK'";

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(insert);
                 PreparedStatement ps2 = c.prepareStatement(updateTotal);
                 PreparedStatement ps3 = c.prepareStatement(updateStock)) {

                ps1.setLong(1, item.getOrderId());
                ps1.setLong(2, item.getItemId());
                ps1.setInt(3, item.getKolicina());
                ps1.executeUpdate();

                ps2.setLong(1, item.getItemId());
                ps2.setInt(2, item.getKolicina());
                ps2.setLong(3, item.getOrderId());
                ps2.executeUpdate();

                ps3.setInt(1, item.getKolicina());
                ps3.setLong(2, item.getItemId());
                ps3.executeUpdate();

                c.commit();
                return true;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("addItem failed", e);
        }
    }

    @Override
    public List<Order> findRecent(int limit) {
        String sql = "SELECT id, user_id, datum, ukupno FROM orders ORDER BY datum DESC LIMIT ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> list = new ArrayList<>();
                while (rs.next()) {
                    Order o = new Order(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getTimestamp("datum").toLocalDateTime(),
                            rs.getDouble("ukupno")
                    );
                    list.add(o);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findRecent failed", e);
        }
    }

    @Override
    public List<OrderItem> findItems(long orderId) {
        String sql = "SELECT oi.id, oi.order_id, oi.item_id, mi.naziv, oi.kolicina, mi.cijena FROM order_items oi JOIN menu_items mi ON mi.id = oi.item_id WHERE oi.order_id=?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new OrderItem(
                            rs.getLong(1),
                            rs.getLong(2),
                            rs.getLong(3),
                            rs.getString(4),
                            rs.getInt(5),
                            rs.getDouble(6)
                    ));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findItems failed", e);
        }
    }
}
