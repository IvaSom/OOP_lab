package ru.ssau.tk.swc.labs.dto;

public class AnalPointsDTO {
    private Long id;
    private Double x;
    private Double y;
    private Long functionId; // Only ID instead of full object

    public AnalPointsDTO() {}

    public AnalPointsDTO(Long id, Double x, Double y, Long functionId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.functionId = functionId;
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