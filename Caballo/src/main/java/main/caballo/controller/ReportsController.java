package main.caballo.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import main.caballo.Main;
import main.caballo.dao.ReportDao;
import main.caballo.dao.impl.ReportDaoImpl;
import main.caballo.model.DailyItemReport;
import main.caballo.util.PdfReportUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReportsController {
    @FXML
    private DatePicker datePicker;
    @FXML private Label totalLabel;
    @FXML private ListView<String> topItemsList;
    @FXML private ListView<String> salesByWaiterList;

    @FXML private TableView<DailyItemReport> dailyItemsTable;
    @FXML private TableColumn<DailyItemReport, String> colItemName;
    @FXML private TableColumn<DailyItemReport, Integer> colOpeningQty;
    @FXML private TableColumn<DailyItemReport, Integer> colReceivedQty;
    @FXML private TableColumn<DailyItemReport, Integer> colSoldQty;
    @FXML private TableColumn<DailyItemReport, Integer> colExpectedClosingQty;
    @FXML private TableColumn<DailyItemReport, Integer> colPhysicalClosingQty;
    @FXML private TableColumn<DailyItemReport, Double> colSoldTotalAmount;
    @FXML private TableColumn<DailyItemReport, String> colNote;

    private final ReportDao dao = new ReportDaoImpl();

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    return;
                }
                setDisable(item.isAfter(LocalDate.now()));
            }
        });

        dailyItemsTable.setEditable(true);

        colItemName.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getItemName()));
        colOpeningQty.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getOpeningQty()).asObject());
        colReceivedQty.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getReceivedQty()).asObject());
        colSoldQty.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getSoldQty()).asObject());
        colExpectedClosingQty.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getExpectedClosingQty()).asObject());
        colPhysicalClosingQty.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getPhysicalClosingQty()).asObject());
        colSoldTotalAmount.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getSoldTotalAmount()).asObject());
        colNote.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getNote()));

        colPhysicalClosingQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colPhysicalClosingQty.setOnEditCommit(evt -> {
            DailyItemReport row = evt.getRowValue();
            Integer newValue = evt.getNewValue() != null ? evt.getNewValue() : 0;
            row.setPhysicalClosingQty(newValue);

            LocalDate d = datePicker.getValue();
            if (d != null && row.getItemName() != null) {
                dao.updatePhysicalQtyAndNote(d, row.getItemName(), row.getPhysicalClosingQty(), row.getNote());
            }
            dailyItemsTable.refresh();
        });

        colNote.setCellFactory(TextFieldTableCell.forTableColumn());
        colNote.setOnEditCommit(evt -> {
            DailyItemReport row = evt.getRowValue();
            String newNote = evt.getNewValue();
            row.setNote(newNote);

            LocalDate d = datePicker.getValue();
            if (d != null && row.getItemName() != null) {
                dao.updatePhysicalQtyAndNote(d, row.getItemName(), row.getPhysicalClosingQty(), row.getNote());
            }
            dailyItemsTable.refresh();
        });

        load();
    }

    @FXML
    private void onCloseDayClicked(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            new Alert(Alert.AlertType.WARNING, "Odaberi datum za zatvaranje dana.", ButtonType.OK).showAndWait();
            return;
        }

        dao.ensureItemStockForDate(date);
        dao.closeDayStock(date);
        load();

        new Alert(Alert.AlertType.INFORMATION, "Dan " + date + " je uspješno zatvoren.", ButtonType.OK).showAndWait();
    }

    private void load() {
        LocalDate d = datePicker.getValue();

        dao.ensureItemStockForDate(d);

        double total = dao.totalForDate(d);
        totalLabel.setText(String.format("Ukupno: %.2f KM", total));

        var top = dao.topItemsForDate(d, 10);
        topItemsList.setItems(FXCollections.observableArrayList(
                top.stream()
                        .map(e -> e.getKey() + " x " + e.getValue())
                        .toList()
        ));

        var byWaiter = dao.salesByWaiter(d);
        salesByWaiterList.setItems(FXCollections.observableArrayList(
                byWaiter.stream()
                        .map(e -> e.getKey() + ": " + String.format("%.2f", e.getValue()) + " KM")
                        .toList()
        ));

        List<DailyItemReport> items = dao.dailyItemReport(d);
        dailyItemsTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void loadData(ActionEvent e) {
        load();
    }

    @FXML
    private void export(ActionEvent e) {
        try {
            LocalDate d = datePicker.getValue();
            double total = dao.totalForDate(d);
            var items = dao.dailyItemReport(d);

            Path out = Path.of("C:/reports", "caballo-daily-" + d + ".pdf");
            Files.createDirectories(out.getParent());
            PdfReportUtil.generateDailySales(d, total, items, out);

            new Alert(Alert.AlertType.INFORMATION, "Arhivirano u: " + out.toAbsolutePath(), ButtonType.OK).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Export nije uspio: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void goBack(ActionEvent e) {
        Main.showDashboard();
    }
}
