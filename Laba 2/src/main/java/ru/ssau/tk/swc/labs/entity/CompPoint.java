package ru.ssau.tk.swc.labs.entity;

public class CompPoint {
    private Long id;
    private double x;
    private double y;
    private double derive;
    private Long funID;

    public CompPoint(){}

    public CompPoint(Long id, double x, double y, double derive, Long funID){
        this.id = id;
        this.x = x;
        this.y = y;
        this.derive = derive;
        this.funID = funID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDerive(double derive) {
        this.derive = derive;
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

    public double getDerive() {
        return derive;
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

    @Override
    public String toString() {
        return "AnalPoint{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", derive=" + derive +
                ", funID=" + funID +
                '}';
    }
}
