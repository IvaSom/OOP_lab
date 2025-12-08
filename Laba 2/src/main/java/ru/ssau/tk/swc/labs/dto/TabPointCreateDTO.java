package ru.ssau.tk.swc.labs.dto;

public class TabPointCreateDTO {
    private Double x;
    private Long functionId;

    public TabPointCreateDTO() {}

    public TabPointCreateDTO(Double x, Long functionId) {
        this.x = x;
        this.functionId = functionId;
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }
}