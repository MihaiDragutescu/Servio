package com.servio.models;

public class Table {
    private String tableId;
    private int tableNumber;
    private int tableNumberOfSeats;
    private boolean tableAvailability;

    public Table() {
    }

    public Table(String tableId, int tableNumber, int numberOfSeats, boolean tableAvailability) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.tableNumberOfSeats = numberOfSeats;
        this.tableAvailability = tableAvailability;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void settableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getTableNumberOfSeats() {
        return tableNumberOfSeats;
    }

    public void setNumberOfSeats(int tableNumberOfSeats) {
        this.tableNumberOfSeats = tableNumberOfSeats;
    }

    public boolean isTableAvailability() {
        return tableAvailability;
    }

    public void setTableAvailability(boolean tableAvailability) {
        this.tableAvailability = tableAvailability;
    }
}
