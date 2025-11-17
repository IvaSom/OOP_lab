package ru.ssau.tk.swc.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tabFun")
public class tabFun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    public tabFun(){
    }

    public tabFun(Integer type){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(Integer type) {
        this.name = name;
    }
}
