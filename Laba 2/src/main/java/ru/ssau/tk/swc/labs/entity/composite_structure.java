package ru.ssau.tk.swc.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "composite_structure")
public class composite_structure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //композитная функцией
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composite_id", nullable = false)
    private compFun compositeFunction;

    //аналитическая функцией
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analytic_id", nullable = false)
    private analFun analyticFunction;

    @Column(name = "execution_order", nullable = false)
    private Integer executionOrder;

    public composite_structure() {
    }

    public composite_structure(compFun compositeFunction, analFun analyticFunction, Integer executionOrder) {
        this.compositeFunction = compositeFunction;
        this.analyticFunction = analyticFunction;
        this.executionOrder = executionOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public compFun getCompFun() {
        return compositeFunction;
    }

    public void setCompFun(compFun compositeFunction) {
        this.compositeFunction = compositeFunction;
    }

    public analFun getAnalFun() {
        return analyticFunction;
    }

    public void setAnalFun(analFun analyticFunction) {
        this.analyticFunction = analyticFunction;
    }

    public Integer getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Integer executionOrder) {
        this.executionOrder = executionOrder;
    }
}
