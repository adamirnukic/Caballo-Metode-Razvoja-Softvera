package main.caballo.model;

public class DailyItemReport {
    private String itemName;
    private int openingQty;
    private int receivedQty;
    private int soldQty;
    private int expectedClosingQty;
    private int physicalClosingQty;
    private double soldTotalAmount;

    public DailyItemReport() {}

    public DailyItemReport(String itemName, int openingQty, int receivedQty, int soldQty, int expectedClosingQty, int physicalClosingQty, double soldTotalAmount) {
        this.itemName = itemName;
        this.openingQty = openingQty;
        this.receivedQty = receivedQty;
        this.soldQty = soldQty;
        this.expectedClosingQty = expectedClosingQty;
        this.physicalClosingQty = physicalClosingQty;
        this.soldTotalAmount = soldTotalAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getOpeningQty() {
        return openingQty;
    }

    public void setOpeningQty(int openingQty) {
        this.openingQty = openingQty;
    }

    public int getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(int receivedQty) {
        this.receivedQty = receivedQty;
    }

    public int getSoldQty() {
        return soldQty;
    }

    public void setSoldQty(int soldQty) {
        this.soldQty = soldQty;
    }

    public int getExpectedClosingQty() {
        return expectedClosingQty;
    }

    public void setExpectedClosingQty(int expectedClosingQty) {
        this.expectedClosingQty = expectedClosingQty;
    }

    public int getPhysicalClosingQty() {
        return physicalClosingQty;
    }

    public void setPhysicalClosingQty(int physicalClosingQty) {
        this.physicalClosingQty = physicalClosingQty;
    }

    public double getSoldTotalAmount() {
        return soldTotalAmount;
    }

    public void setSoldTotalAmount(double soldTotalAmount) {
        this.soldTotalAmount = soldTotalAmount;
    }
}
