package ru.ssau.tk.swc.labs.dto;

public class AnalPointsCreateDTO {
    private Double x;
    private Double y;
    private Long functionId;

    public AnalPointsCreateDTO() {}

    public AnalPointsCreateDTO(Double x, Double y, Long functionId) {
        this.x = x;
        this.y = y;
        this.functionId = functionId;
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }
}