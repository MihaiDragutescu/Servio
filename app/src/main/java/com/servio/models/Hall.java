package com.servio.models;

public class Hall {
    private String hallId;
    private String hallName;
    private int hallNumber;
    private String hallSize;
    private String cellCollectionName;

    public Hall() {
    }

    public Hall(String hallId, String hallName, int hallNumber, String hallSize, String cellCollectionName) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.hallNumber = hallNumber;
        this.hallSize = hallSize;
        this.cellCollectionName = cellCollectionName;
    }

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    public String getHallSize() {
        return hallSize;
    }

    public void setHallSize(String hallSize) {
        this.hallSize = hallSize;
    }

    public String getCellCollectionName() {
        return cellCollectionName;
    }

    public void setCellCollectionName(String cellCollectionName) {
        this.cellCollectionName = cellCollectionName;
    }
}
