package main.caballo.dao;

import main.caballo.model.Order;
import main.caballo.model.OrderItem;

import java.util.List;

public interface OrderDao {
    Order create(Order o);
    boolean addItem(OrderItem item);
    List<Order> findRecent(int limit);
    List<OrderItem> findItems(long orderId);
}

