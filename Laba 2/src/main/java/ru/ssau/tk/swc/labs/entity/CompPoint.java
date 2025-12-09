package ru.ssau.tk.swc.labs.entity;

public class CompPoint {
    private Long id;
    private Long funID;
    private Double x;
    private Double y;

    public CompPoint(){}

    public CompPoint(Long id, double x, double y, Long funID){
        this.id = id;
        this.x = x;
        this.y = y;
        this.funID = funID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFunID(Long funID) {
        this.funID = funID;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Long getFunID() {
        return funID;
    }
}
