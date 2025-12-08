package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;

public class TabFunDTO {
    private Long id;
    private String name;

    public TabFunDTO() {}

    public TabFunDTO(String name) {
        this.name = name;
    }

    public TabFunDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public TabFunDTO(tabFun entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}