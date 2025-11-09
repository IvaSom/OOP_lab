package ru.ssau.tk.swc.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "anal_points")
public class anal_points {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name = "y", nullable = false)
    private Double y;

    @Column(name = "derive", nullable = false)
    private Double derive;

    @ManyToOne(fetch = FetchType.LAZY) //fetch - настройка когда загружать связаный объект
    //lazy значит загружается только когда обратщение к методу где надо function
    @JoinColumn(name = "funID", nullable = false) //связывает с колонкой
    private analFun function;

    // Конструкторы
    public anal_points() {
    }

    public anal_points(Double x, Double y, Double derive, analFun function) {
        this.x = x;
        this.y = y;
        this.derive = derive;
        this.function = function;
    }

    // Геттеры и сеттеры
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

    public analFun getFunction() {
        return function;
    }

    public void setFunction(analFun function) {
        this.function = function;
    }
}
