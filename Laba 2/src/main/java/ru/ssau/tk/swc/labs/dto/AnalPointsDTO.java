package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;

public class AnalPointsDTO {
    private Long id;
    private Double x;
    private Double y;
    private Long functionId;

    public AnalPointsDTO() {}

    public AnalPointsDTO(Long id, Double x, Double y, Long functionId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.functionId = functionId;
    }
    public AnalPointsDTO(anal_points entity) {
        this.id = entity.getId();
        this.x = entity.getX();
        this.y = entity.getY();
        this.functionId = entity.getFunction().getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }
}