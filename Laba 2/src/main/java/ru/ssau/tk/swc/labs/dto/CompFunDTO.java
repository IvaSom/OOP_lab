package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;

public class CompFunDTO {
    private Long id;
    private String name;

    public CompFunDTO() {}

    public CompFunDTO(String name) {
        this.name = name;
    }

    public CompFunDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public CompFunDTO(compFun entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}