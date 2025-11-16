package ru.ssau.tk.swc.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "composite_structure")
public class CompositeStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "composite_id", nullable = false)
    private Long composite_id;

    @Column(name = "analytic_id", nullable = false)
    private Long analytic_id;

    @Column(name = "order", nullable = false)
    private int order;

    public CompositeStructure(){}

    public CompositeStructure(Long id, Long composite_id, Long analytic_id, int order) {
        this.id = id;
        this.composite_id = composite_id;
        this.analytic_id = analytic_id;
        this.order = order;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnalytic_id(Long analytic_id) {
        this.analytic_id = analytic_id;
    }

    public void setComposite_id(Long composite_id) {
        this.composite_id = composite_id;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public Long getAnalytic_id() {
        return analytic_id;
    }

    public Long getComposite_id() {
        return composite_id;
    }

    @Override
    public String toString() {
        return "CompositeStructure{" +
                "id=" + id +
                ", composite_id=" + composite_id +
                ", analytic_id=" + analytic_id +
                ", order=" + order +
                '}';
    }
}
