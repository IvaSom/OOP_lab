package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;

public class AnalFunDTO {
    private Long id;
    private String name;
    private Integer type;

    public AnalFunDTO() {}

    public AnalFunDTO(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public AnalFunDTO(Long id, String name, Integer type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public AnalFunDTO(analFun entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.type = entity.getType();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
}