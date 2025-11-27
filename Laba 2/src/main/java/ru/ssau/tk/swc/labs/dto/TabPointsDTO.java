package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;

public class TabPointsDTO {
    private Long id;
    private Double x;
    private Double y;
    private Double derive;
    private Long functionId;

    public TabPointsDTO() {}

    public TabPointsDTO(Long id, Double x, Double y, Double derive, Long functionId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.derive = derive;
        this.functionId = functionId;
    }
    public TabPointsDTO(tab_points entity) {
        this.id = entity.getId();
        this.x = entity.getX();
        this.y = entity.getY();
        this.derive = entity.getDerive();
        this.functionId = entity.getFunction().getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Double getDerive() { return derive; }
    public void setDerive(Double derive) { this.derive = derive; }
    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }
}