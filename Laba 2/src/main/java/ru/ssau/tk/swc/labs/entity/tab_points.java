package ru.ssau.tk.swc.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tab_points")
public class tab_points {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name = "y", nullable = false)
    private Double y;

    @Column(name = "derive", nullable = false)
    private Double derive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funID", nullable = false)
    private tabFun function;


    public tab_points() {
    }

    public tab_points(Double x, Double y, Double derive, tabFun function) {
        this.x = x;
        this.y = y;
        this.derive = derive;
        this.function = function;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getDerive() {
        return derive;
    }

    public void setDerive(Double derive) {
        this.derive = derive;
    }

    public tabFun getFunction() {
        return function;
    }

    public void setFunction(tabFun function) {
        this.function = function;
    }
}
