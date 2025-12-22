package main.caballo.dao;

import main.caballo.model.DailyItemReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportDao {
    double totalForDate(LocalDate date);
    List<Map.Entry<String, Integer>> topItemsForDate(LocalDate date, int limit);
    List<Map.Entry<String, Double>> salesByWaiter(LocalDate date);
    List<DailyItemReport> dailyItemReport(LocalDate date);
    void ensureItemStockForDate(LocalDate date);
    void closeDayStock(LocalDate date);
    void addReceivedQty(LocalDate date, long itemId, int quantity);
    void updatePhysicalQtyAndNote(LocalDate date, String itemName, int physicalQty, String note);
}