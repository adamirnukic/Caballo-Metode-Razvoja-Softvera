package main.caballo.dao;

import main.caballo.model.MenuItem;

import java.util.List;

public interface MenuItemDao {
    MenuItem create(MenuItem item);
    boolean update(MenuItem item);
    boolean delete(long id);
    List<MenuItem> findAll();
    List<MenuItem> search(String query, String category);
}
