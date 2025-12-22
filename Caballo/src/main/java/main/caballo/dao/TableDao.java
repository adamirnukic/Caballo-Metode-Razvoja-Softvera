package main.caballo.dao;

import main.caballo.model.DiningTable;
import java.util.List;

public interface TableDao {
    List<DiningTable> findAll();
}