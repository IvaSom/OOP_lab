package ru.ssau.tk.swc.labs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "analFun") //указываем имя таблицы в базе данных

public class analFun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //для id ключа
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "type")
    private Integer type;

    public analFun() {
        //пустой конструктор вроде как нужен обязательно
    }

    public analFun(String name, Integer type) {
        this.name = name;
        this.type = type;
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

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}